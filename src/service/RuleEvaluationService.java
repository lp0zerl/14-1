package service;

import entity.RuleConditionEntity;
import type.QueryType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
class RuleEvaluationService {
    @Autowired
    private com.recommendation.UserProductRepository userProductRepository;

    @Cacheable(value = "conditionEvaluationCache",
            key = "#userId.toString() + '_' + #condition.hashCode()")
    public boolean evaluateCondition(UUID userId, RuleConditionEntity condition) {
        QueryType queryType = QueryType.fromCode(condition.getQueryType());
        List<String> args = condition.getArguments();
        boolean result = switch (queryType) {
            case USER_OF -> evaluateUserOf(userId, args);
            case ACTIVE_USER_OF -> evaluateActiveUserOf(userId, args);
            case TRANSACTION_SUM_COMPARE -> evaluateTransactionSumCompare(userId, args);
            case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW -> evaluateDepositWithdrawCompare(userId, args);
        };
        return condition.isNegate() ? !result : result;
    }

    private boolean evaluateUserOf(UUID userId, List<String> arguments) {
        String productType = arguments.get(0);
        return userProductRepository.isUserOfProductType(userId, productType);
    }

    private boolean evaluateActiveUserOf(UUID userId, List<String> arguments) {
        String productType = arguments.get(0);
        return userProductRepository.isActiveUserOfProductType(userId, productType);
    }

    private boolean evaluateTransactionSumCompare(UUID userId, List<String> arguments) {
        String productType = arguments.get(0);
        String transactionType = arguments.get(1);
        String operator = arguments.get(2);
        BigDecimal constant = new BigDecimal(arguments.get(3));
        BigDecimal transactionSum = userProductRepository.getTransactionSumByType(
                userId, productType, transactionType);
        return compareValues(transactionSum, operator, constant);
    }

    private boolean evaluateDepositWithdrawCompare(UUID userId, List<String> arguments) {
        String productType = arguments.get(0);
        String operator = arguments.get(1);
        BigDecimal depositSum = userProductRepository.getDepositSum(userId, productType);
        BigDecimal withdrawSum = userProductRepository.getWithdrawSum(userId, productType);
        return compareValues(depositSum, operator, withdrawSum);
    }

    private boolean compareValues(BigDecimal value1, String operator, BigDecimal value2) {
        int comparison = value1.compareTo(value2);
        switch (operator) {
            case ">":
                return comparison > 0;
            case "<":
                return comparison < 0;
            case "=":
                return comparison == 0;
            case ">=":
                return comparison >= 0;
            case "<=":
                return comparison <= 0;
            default:
                throw new IllegalArgumentException("Неизвестный оператор: " + operator);
        }
    }

    public boolean evaluateRule(UUID userId, List<RuleConditionEntity> conditions) {
        for (RuleConditionEntity condition : conditions) {
            if (!evaluateCondition(userId, condition)) {
                return false;
            }
        }
        return true;
    }
}
