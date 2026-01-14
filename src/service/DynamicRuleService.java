package service;

import entity.DynamicRuleEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
class DynamicRuleService {
    @Autowired
    private com.recommendation.DynamicRuleRepository ruleRepository;

    @CacheEvict(value = {"ruleCache", "recommendationCache"}, allEntries = true)
    public DynamicRuleEntity createRule(DynamicRuleEntity rule) {
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        return ruleRepository.save(rule);
    }

    @CacheEvict(value = {"ruleCache", "recommendationCache"}, allEntries = true)
    public DynamicRuleEntity updateRule(UUID ruleId, DynamicRuleEntity updatedRule) {
        DynamicRuleEntity existingRule = getRuleById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Правило не найдено: " + ruleId));
        existingRule.setProductName(updatedRule.getProductName());
        existingRule.setProductId(updatedRule.getProductId());
        existingRule.setProductText(updatedRule.getProductText());
        existingRule.setRule(updatedRule.getRule());
        existingRule.setActive(updatedRule.isActive());
        existingRule.setPriority(updatedRule.getPriority());
        existingRule.setDescription(updatedRule.getDescription());
        existingRule.setUpdatedAt(LocalDateTime.now());
        return ruleRepository.save(existingRule);
    }

    @Cacheable(value = "ruleCache", key = "#ruleId")
    @Transactional(readOnly = true)
    public Optional<DynamicRuleEntity> getRuleById(UUID ruleId) {
        return ruleRepository.findById(ruleId);
    }

    @Cacheable(value = "ruleCache", key = "'all_active'")
    @Transactional(readOnly = true)
    public List<DynamicRuleEntity> getAllActiveRules() {
        return ruleRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public List<DynamicRuleEntity> getAllRules() {
        return ruleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<DynamicRuleEntity> getRulesPage(Pageable pageable) {
        return ruleRepository.findAll(pageable);
    }

    @CacheEvict(value = {"ruleCache", "recommendationCache"}, allEntries = true)
    public void deleteRule(UUID ruleId) {
        ruleRepository.deleteById(ruleId);
    }

    @CacheEvict(value = {"ruleCache", "recommendationCache"}, allEntries = true)
    public void deactivateRule(UUID ruleId) {
        DynamicRuleEntity rule = getRuleById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Правило не найдено: " + ruleId));
        rule.setActive(false);
        rule.setUpdatedAt(LocalDateTime.now());
        ruleRepository.save(rule);
    }

    @CacheEvict(value = {"ruleCache", "recommendationCache"}, allEntries = true)
    public void activateRule(UUID ruleId) {
        DynamicRuleEntity rule = getRuleById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Правило не найдено: " + ruleId));
        rule.setActive(true);
        rule.setUpdatedAt(LocalDateTime.now());
        ruleRepository.save(rule);
    }

    @Transactional(readOnly = true)
    public boolean ruleExists(UUID ruleId) {
        return ruleRepository.existsById(ruleId);
    }

    @Transactional(readOnly = true)
    public DynamicRuleService.RuleStatistics getRuleStatistics() {
        long totalRules = ruleRepository.count();
        long activeRules = ruleRepository.countActive();
        DynamicRuleService.RuleStatistics stats = new DynamicRuleService.RuleStatistics();
        stats.setTotalRules(totalRules);
        stats.setActiveRules(activeRules);
        stats.setInactiveRules(totalRules - activeRules);
        return stats;
    }

    public static class RuleStatistics {
        private long totalRules;
        private long activeRules;
        private long inactiveRules;

        public long getTotalRules() {
            return totalRules;
        }

        public void setTotalRules(long totalRules) {
            this.totalRules = totalRules;
        }

        public long getActiveRules() {
            return activeRules;
        }

        public void setActiveRules(long activeRules) {
            this.activeRules = activeRules;
        }

        public long getInactiveRules() {
            return inactiveRules;
        }

        public void setInactiveRules(long inactiveRules) {
            this.inactiveRules = inactiveRules;
        }
    }
}
