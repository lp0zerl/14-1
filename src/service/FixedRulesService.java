package service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly = true)
class FixedRulesService {
    @Autowired
    private com.recommendation.UserProductRepository userProductRepository;

    @Cacheable(value = "fixedRecommendationsCache", key = "#userId")
    public List<RecommendationService.Recommendation> getFixedRecommendations(UUID userId) {
        List<RecommendationService.Recommendation> recommendations = new ArrayList<>();
        if (shouldRecommendCreditCard(userId)) {
            recommendations.add(createCreditCardRecommendation());
        }
        if (shouldRecommendSavingsAccount(userId)) {
            recommendations.add(createSavingsAccountRecommendation());
        }
        if (shouldRecommendInvestmentAccount(userId)) {
            recommendations.add(createInvestmentAccountRecommendation());
        }
        if (shouldRecommendPremiumDebitCard(userId)) {
            recommendations.add(createPremiumDebitCardRecommendation());
        }
        return recommendations;
    }

    private boolean shouldRecommendCreditCard(UUID userId) {
        try {
            boolean isCreditUser = userProductRepository.isUserOfProductType(userId, "CREDIT");
            return !isCreditUser;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean shouldRecommendSavingsAccount(UUID userId) {
        try {
            boolean isActiveDebitUser = userProductRepository.isActiveUserOfProductType(userId, "DEBIT");
            if (!isActiveDebitUser) {
                return false;
            }
            BigDecimal depositSum = userProductRepository.getDepositSum(userId, "DEBIT");
            BigDecimal withdrawSum = userProductRepository.getWithdrawSum(userId, "DEBIT");
            BigDecimal difference = depositSum.subtract(withdrawSum);
            return difference.compareTo(new BigDecimal("50000")) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean shouldRecommendInvestmentAccount(UUID userId) {
        try {
            BigDecimal totalDeposits = BigDecimal.ZERO;
            for (String productType : Arrays.asList("DEBIT", "SAVING")) {
                totalDeposits = totalDeposits.add(
                        userProductRepository.getDepositSum(userId, productType)
                );
            }
            return totalDeposits.compareTo(new BigDecimal("500000")) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean shouldRecommendPremiumDebitCard(UUID userId) {
        try {
            long totalTransactions = 0;
            for (String productType : Arrays.asList("DEBIT", "CREDIT")) {
                totalTransactions += userProductRepository.getTransactionSumByType(userId, productType, "DEPOSIT")
                        .add(userProductRepository.getTransactionSumByType(userId, productType, "WITHDRAW"))
                        .longValue();
            }
            if (totalTransactions < 100) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private RecommendationService.Recommendation createCreditCardRecommendation() {
        RecommendationService.Recommendation recommendation = new RecommendationService.Recommendation();
        recommendation.setProductId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        recommendation.setProductName("Кредитная карта Platinum");
        recommendation.setProductText("Кредитная карта с льготным периодом 100 дней и кешбэком до 10%");
        recommendation.setPriority(90);
        recommendation.setDynamic(false);
        recommendation.setGeneratedAt(new Date());
        return recommendation;
    }

    private RecommendationService.Recommendation createSavingsAccountRecommendation() {
        RecommendationService.Recommendation recommendation = new RecommendationService.Recommendation();
        recommendation.setProductId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        recommendation.setProductName("Накопительный счет 'Выгодный'");
        recommendation.setProductText("Накопительный счет с процентной ставкой 8% годовых");
        recommendation.setPriority(85);
        recommendation.setDynamic(false);
        recommendation.setGeneratedAt(new Date());
        return recommendation;
    }

    private RecommendationService.Recommendation createInvestmentAccountRecommendation() {
        RecommendationService.Recommendation recommendation = new RecommendationService.Recommendation();
        recommendation.setProductId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        recommendation.setProductName("Инвестиционный портфель 'Рост'");
        recommendation.setProductText("Сбалансированный инвестиционный портфель");
        recommendation.setPriority(80);
        recommendation.setDynamic(false);
        recommendation.setGeneratedAt(new Date());
        return recommendation;
    }

    private RecommendationService.Recommendation createPremiumDebitCardRecommendation() {
        RecommendationService.Recommendation recommendation = new RecommendationService.Recommendation();
        recommendation.setProductId(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        recommendation.setProductName("Дебетовая карта Premium");
        recommendation.setProductText("Премиальная дебетовая карта с повышенным кешбэком");
        recommendation.setPriority(95);
        recommendation.setDynamic(false);
        recommendation.setGeneratedAt(new Date());
        return recommendation;
    }
}
