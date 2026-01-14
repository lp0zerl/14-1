package service;

import entity.DynamicRuleEntity;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
class RecommendationService {
    @Autowired
    private DynamicRuleService ruleService;
    @Autowired
    private RuleEvaluationService ruleEvaluationService;
    @Autowired
    private com.recommendation.FixedRulesService fixedRulesService;

    @Cacheable(value = "recommendationCache", key = "#userId")
    public RecommendationService.RecommendationResult getRecommendations(UUID userId) {
        List<DynamicRuleEntity> activeRules = ruleService.getAllActiveRules();
        List<RecommendationService.Recommendation> dynamicRecommendations = evaluateDynamicRules(userId, activeRules);
        List<RecommendationService.Recommendation> fixedRecommendations = fixedRulesService.getFixedRecommendations(userId);
        List<RecommendationService.Recommendation> allRecommendations = new ArrayList<>();
        allRecommendations.addAll(dynamicRecommendations);
        allRecommendations.addAll(fixedRecommendations);
        allRecommendations.sort(Comparator.comparingInt(RecommendationService.Recommendation::getPriority).reversed());
        RecommendationService.RecommendationResult result = new RecommendationService.RecommendationResult();
        result.setUserId(userId);
        result.setRecommendations(allRecommendations);
        result.setTotalRecommendations(allRecommendations.size());
        result.setDynamicRecommendations(dynamicRecommendations.size());
        result.setFixedRecommendations(fixedRecommendations.size());
        result.setGeneratedAt(new Date());
        return result;
    }

    private List<RecommendationService.Recommendation> evaluateDynamicRules(UUID userId, List<DynamicRuleEntity> rules) {
        return rules.parallelStream()
                .filter(rule -> ruleEvaluationService.evaluateRule(userId, rule.getRule()))
                .map(this::convertToRecommendation)
                .collect(Collectors.toList());
    }

    private RecommendationService.Recommendation convertToRecommendation(DynamicRuleEntity rule) {
        RecommendationService.Recommendation recommendation = new RecommendationService.Recommendation();
        recommendation.setProductId(rule.getProductId());
        recommendation.setProductName(rule.getProductName());
        recommendation.setProductText(rule.getProductText());
        recommendation.setPriority(rule.getPriority());
        recommendation.setRuleId(rule.getId());
        recommendation.setRuleDescription(rule.getDescription());
        recommendation.setDynamic(true);
        recommendation.setGeneratedAt(new Date());
        return recommendation;
    }

    public static class RecommendationResult {
        private UUID userId;
        private List<RecommendationService.Recommendation> recommendations;
        private int totalRecommendations;
        private int dynamicRecommendations;
        private int fixedRecommendations;
        private Date generatedAt;

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public List<RecommendationService.Recommendation> getRecommendations() {
            return recommendations;
        }

        public void setRecommendations(List<RecommendationService.Recommendation> recommendations) {
            this.recommendations = recommendations;
        }

        public int getTotalRecommendations() {
            return totalRecommendations;
        }

        public void setTotalRecommendations(int totalRecommendations) {
            this.totalRecommendations = totalRecommendations;
        }

        public int getDynamicRecommendations() {
            return dynamicRecommendations;
        }

        public void setDynamicRecommendations(int dynamicRecommendations) {
            this.dynamicRecommendations = dynamicRecommendations;
        }

        public int getFixedRecommendations() {
            return fixedRecommendations;
        }

        public void setFixedRecommendations(int fixedRecommendations) {
            this.fixedRecommendations = fixedRecommendations;
        }

        public Date getGeneratedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(Date generatedAt) {
            this.generatedAt = generatedAt;
        }
    }

    public static class Recommendation {
        private UUID productId;
        private String productName;
        private String productText;
        private UUID ruleId;
        private String ruleDescription;
        private int priority;
        private boolean isDynamic;
        private Date generatedAt;

        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductText() {
            return productText;
        }

        public void setProductText(String productText) {
            this.productText = productText;
        }

        public UUID getRuleId() {
            return ruleId;
        }

        public void setRuleId(UUID ruleId) {
            this.ruleId = ruleId;
        }

        public String getRuleDescription() {
            return ruleDescription;
        }

        public void setRuleDescription(String ruleDescription) {
            this.ruleDescription = ruleDescription;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public boolean isDynamic() {
            return isDynamic;
        }

        public void setDynamic(boolean dynamic) {
            isDynamic = dynamic;
        }

        public Date getGeneratedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(Date generatedAt) {
            this.generatedAt = generatedAt;
        }
    }
}
