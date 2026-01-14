package service;

import entity.DynamicRuleEntity;
import entity.RuleConditionEntity;
import type.ProductType;
import type.QueryType;
import type.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
class ValidationService {
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    public void validateRule(DynamicRuleEntity rule) {
        if (rule.getProductName() == null || rule.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя продукта не может быть пустым");
        }
        validateProductId(rule.getProductId());
        if (rule.getProductText() != null && rule.getProductText().length() > 5000) {
            throw new IllegalArgumentException("Текст продукта слишком длинный");
        }
        validateRuleConditions(rule.getRule());
        if (rule.getPriority() < 0 || rule.getPriority() > 100) {
            throw new IllegalArgumentException("Приоритет должен быть в диапазоне 0-100");
        }
    }

    public void validateRuleConditions(List<RuleConditionEntity> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            throw new IllegalArgumentException("Правило должно содержать хотя бы одно условие");
        }
        if (conditions.size() > 20) {
            throw new IllegalArgumentException("Слишком много условий в правиле");
        }
        for (int i = 0; i < conditions.size(); i++) {
            RuleConditionEntity condition = conditions.get(i);
            validateCondition(condition);
            condition.setOrder(i);
        }
    }

    public void validateCondition(RuleConditionEntity condition) {
        if (!QueryType.isValid(condition.getQueryType())) {
            throw new IllegalArgumentException("Неизвестный тип запроса: " + condition.getQueryType());
        }
        validateConditionArguments(condition);
    }

    public void validateConditionArguments(RuleConditionEntity condition) {
        List<String> arguments = condition.getArguments();
        if (arguments == null) {
            throw new IllegalArgumentException("Аргументы не могут быть null");
        }
        QueryType queryType = QueryType.fromCode(condition.getQueryType());
        if (arguments.size() < queryType.getMinArguments() ||
                arguments.size() > queryType.getMaxArguments()) {
            throw new IllegalArgumentException(
                    String.format("Неверное количество аргументов для запроса %s", queryType.getCode())
            );
        }
        switch (queryType) {
            case USER_OF:
            case ACTIVE_USER_OF:
                validateProductTypeArgument(arguments.get(0));
                break;
            case TRANSACTION_SUM_COMPARE:
                validateProductTypeArgument(arguments.get(0));
                validateTransactionTypeArgument(arguments.get(1));
                validateComparisonOperatorArgument(arguments.get(2));
                validateConstantArgument(arguments.get(3));
                break;
            case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW:
                validateProductTypeArgument(arguments.get(0));
                validateComparisonOperatorArgument(arguments.get(1));
                break;
        }
        for (String arg : arguments) {
            if (arg != null && arg.length() > 100) {
                throw new IllegalArgumentException("Аргумент слишком длинный");
            }
        }
    }

    private void validateProductTypeArgument(String productType) {
        if (!ProductType.isValid(productType)) {
            throw new IllegalArgumentException("Некорректный тип продукта: " + productType);
        }
    }

    private void validateTransactionTypeArgument(String transactionType) {
        if (!TransactionType.isValid(transactionType)) {
            throw new IllegalArgumentException("Некорректный тип транзакции: " + transactionType);
        }
    }

    private void validateComparisonOperatorArgument(String operator) {
        if (!com.recommendation.ComparisonOperator.isValid(operator)) {
            throw new IllegalArgumentException("Некорректный оператор сравнения: " + operator);
        }
    }

    private void validateConstantArgument(String constantStr) {
        try {
            BigDecimal constant = new BigDecimal(constantStr);
            if (constant.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Константа не может быть отрицательной: " + constantStr);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное числовое значение: " + constantStr);
        }
    }

    public void validateProductId(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("ID продукта не может быть null");
        }
    }

    public void validateUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID пользователя не может быть null");
        }
        if (!UUID_PATTERN.matcher(userId.toString()).matches()) {
            throw new IllegalArgumentException("Некорректный формат UUID: " + userId);
        }
    }

    public UUID validateAndParseUuid(String uuidStr) {
        if (uuidStr == null || uuidStr.trim().isEmpty()) {
            throw new IllegalArgumentException("UUID не может быть пустым");
        }
        if (!UUID_PATTERN.matcher(uuidStr).matches()) {
            throw new IllegalArgumentException("Некорректный формат UUID: " + uuidStr);
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректный UUID: " + uuidStr, e);
        }
    }
}
