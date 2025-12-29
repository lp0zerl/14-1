package com.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@EnableCaching
public class RecommendationApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecommendationApplication.class, args);
    }
}

@Configuration
@EnableJpaRepositories(
        basePackages = "com.recommendation.rule.repository",
        entityManagerFactoryRef = "ruleEntityManager",
        transactionManagerRef = "ruleTransactionManager"
)
class DatabaseConfig {
    @Primary
    @Bean(name = "primaryDataSourceProperties")
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource(
            @Qualifier("primaryDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.recommendation.model")
                .persistenceUnit("primaryPU")
                .properties(jpaProperties())
                .build();
    }
    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
    @Bean(name = "secondaryDataSourceProperties")
    @ConfigurationProperties("spring.secondary.datasource")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Bean(name = "secondaryDataSource")
    public DataSource secondaryDataSource(
            @Qualifier("secondaryDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
    @Bean(name = "ruleEntityManager")
    public LocalContainerEntityManagerFactoryBean ruleEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("secondaryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.recommendation.rule.entity")
                .persistenceUnit("secondaryPU")
                .properties(jpaProperties())
                .build();
    }
    @Bean(name = "ruleTransactionManager")
    public PlatformTransactionManager ruleTransactionManager(
            @Qualifier("ruleEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
    private Map<String, Object> jpaProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        return properties;
    }
}

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10000));
        cacheManager.setCacheNames(Arrays.asList(
                "userOfCache",
                "activeUserOfCache",
                "transactionSumCache",
                "depositWithdrawCache",
                "ruleCache",
                "recommendationCache"
        ));
        return cacheManager;
    }
}

enum ProductType {
    DEBIT, CREDIT, INVEST, SAVING;
    public static boolean isValid(String type) {
        for (ProductType productType : values()) {
            if (productType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}

enum TransactionType {
    DEPOSIT, WITHDRAW;
    public static boolean isValid(String type) {
        for (TransactionType transactionType : values()) {
            if (transactionType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}

enum ComparisonOperator {
    GREATER_THAN(">"),
    LESS_THAN("<"),
    EQUALS("="),
    GREATER_THAN_OR_EQUALS(">="),
    LESS_THAN_OR_EQUALS("<=");
    private final String symbol;
    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }
    public String getSymbol() {
        return symbol;
    }
    public static boolean isValid(String operator) {
        for (ComparisonOperator compOperator : values()) {
            if (compOperator.getSymbol().equals(operator)) {
                return true;
            }
        }
        return false;
    }
    public static ComparisonOperator fromSymbol(String symbol) {
        for (ComparisonOperator operator : values()) {
            if (operator.getSymbol().equals(symbol)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("Неизвестный оператор сравнения: " + symbol);
    }
}

enum QueryType {
    USER_OF("USER_OF", 1, 1),
    ACTIVE_USER_OF("ACTIVE_USER_OF", 1, 1),
    TRANSACTION_SUM_COMPARE("TRANSACTION_SUM_COMPARE", 4, 4),
    TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW", 2, 2);
    private final String code;
    private final int minArguments;
    private final int maxArguments;
    QueryType(String code, int minArguments, int maxArguments) {
        this.code = code;
        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
    }
    public String getCode() { return code; }
    public int getMinArguments() { return minArguments; }
    public int getMaxArguments() { return maxArguments; }
    public static QueryType fromCode(String code) {
        for (QueryType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Неизвестный тип запроса: " + code);
    }
    public static boolean isValid(String code) {
        for (QueryType type : values()) {
            if (type.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}

@Embeddable
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
class RuleConditionEntity {
    @Column(name = "query_type", nullable = false)
    @JsonProperty("query")
    private String queryType;
    @Type(type = "jsonb")
    @Column(name = "arguments", columnDefinition = "jsonb")
    @JsonProperty("arguments")
    private List<String> arguments = new ArrayList<>();
    @Column(name = "negate", nullable = false)
    @JsonProperty("negate")
    private boolean negate = false;
    @Column(name = "condition_order", nullable = false)
    @JsonIgnore
    private int order;
    public RuleConditionEntity() {}
    public String getQueryType() { return queryType; }
    public void setQueryType(String queryType) { this.queryType = queryType; }
    public List<String> getArguments() { return arguments; }
    public void setArguments(List<String> arguments) { this.arguments = arguments; }
    public boolean isNegate() { return negate; }
    public void setNegate(boolean negate) { this.negate = negate; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}

@Entity
@Table(name = "dynamic_rules")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@EntityListeners(AuditingEntityListener.class)
class DynamicRuleEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    @Column(name = "product_text", columnDefinition = "TEXT")
    private String productText;
    @Type(type = "jsonb")
    @Column(name = "rule", columnDefinition = "jsonb", nullable = false)
    private List<RuleConditionEntity> rule = new ArrayList<>();
    @Column(name = "version", nullable = false)
    @Version
    private Long version = 0L;
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    @Column(name = "priority", nullable = false)
    private int priority = 0;
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Column(name = "description")
    private String description;
    public DynamicRuleEntity() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }
    public List<RuleConditionEntity> getRule() { return rule; }
    public void setRule(List<RuleConditionEntity> rule) { this.rule = rule; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

@Repository
interface DynamicRuleRepository extends JpaRepository<DynamicRuleEntity, UUID> {
    @Query("SELECT r FROM DynamicRuleEntity r WHERE r.isActive = true ORDER BY r.priority DESC, r.createdAt DESC")
    List<DynamicRuleEntity> findAllActive();
    @Query("SELECT r FROM DynamicRuleEntity r WHERE r.productId = :productId AND r.isActive = true")
    List<DynamicRuleEntity> findActiveByProductId(@Param("productId") UUID productId);
    @Query("SELECT COUNT(r) FROM DynamicRuleEntity r WHERE r.isActive = true")
    long countActive();
}

@Entity
@Table(name = "products")
class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "type", nullable = false)
    private String type;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

@Entity
@Table(name = "transactions")
class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}

@Repository
interface UserProductRepository extends JpaRepository<TransactionEntity, UUID> {
    @Query("SELECT COUNT(DISTINCT p.id) > 0 FROM TransactionEntity t " +
            "JOIN ProductEntity p ON t.productId = p.id " +
            "WHERE t.userId = :userId AND p.type = :productType")
    @Cacheable(value = "userOfCache", key = "#userId.toString() + '_' + #productType")
    boolean isUserOfProductType(@Param("userId") UUID userId,
                                @Param("productType") String productType);
    @Query("SELECT COUNT(t.id) >= 5 FROM TransactionEntity t " +
            "JOIN ProductEntity p ON t.productId = p.id " +
            "WHERE t.userId = :userId AND p.type = :productType")
    @Cacheable(value = "activeUserOfCache", key = "#userId.toString() + '_' + #productType")
    boolean isActiveUserOfProductType(@Param("userId") UUID userId,
                                      @Param("productType") String productType);
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
            "JOIN ProductEntity p ON t.productId = p.id " +
            "WHERE t.userId = :userId AND p.type = :productType AND t.type = :transactionType")
    @Cacheable(value = "transactionSumCache",
            key = "#userId.toString() + '_' + #productType + '_' + #transactionType")
    BigDecimal getTransactionSumByType(@Param("userId") UUID userId,
                                       @Param("productType") String productType,
                                       @Param("transactionType") String transactionType);
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
            "JOIN ProductEntity p ON t.productId = p.id " +
            "WHERE t.userId = :userId AND p.type = :productType AND t.type = 'DEPOSIT'")
    @Cacheable(value = "depositWithdrawCache", key = "#userId.toString() + '_' + #productType + '_deposit'")
    BigDecimal getDepositSum(@Param("userId") UUID userId,
                             @Param("productType") String productType);
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
            "JOIN ProductEntity p ON t.productId = p.id " +
            "WHERE t.userId = :userId AND p.type = :productType AND t.type = 'WITHDRAW'")
    @Cacheable(value = "depositWithdrawCache", key = "#userId.toString() + '_' + #productType + '_withdraw'")
    BigDecimal getWithdrawSum(@Param("userId") UUID userId,
                              @Param("productType") String productType);
}

@Service
@Transactional(readOnly = true)
class RuleEvaluationService {
    @Autowired
    private UserProductRepository userProductRepository;
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
            case ">": return comparison > 0;
            case "<": return comparison < 0;
            case "=": return comparison == 0;
            case ">=": return comparison >= 0;
            case "<=": return comparison <= 0;
            default: throw new IllegalArgumentException("Неизвестный оператор: " + operator);
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

@Service
@Transactional
class DynamicRuleService {
    @Autowired
    private DynamicRuleRepository ruleRepository;
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
    public RuleStatistics getRuleStatistics() {
        long totalRules = ruleRepository.count();
        long activeRules = ruleRepository.countActive();
        RuleStatistics stats = new RuleStatistics();
        stats.setTotalRules(totalRules);
        stats.setActiveRules(activeRules);
        stats.setInactiveRules(totalRules - activeRules);
        return stats;
    }
    public static class RuleStatistics {
        private long totalRules;
        private long activeRules;
        private long inactiveRules;
        public long getTotalRules() { return totalRules; }
        public void setTotalRules(long totalRules) { this.totalRules = totalRules; }
        public long getActiveRules() { return activeRules; }
        public void setActiveRules(long activeRules) { this.activeRules = activeRules; }
        public long getInactiveRules() { return inactiveRules; }
        public void setInactiveRules(long inactiveRules) { this.inactiveRules = inactiveRules; }
    }
}

@Service
@Transactional(readOnly = true)
class RecommendationService {
    @Autowired
    private DynamicRuleService ruleService;
    @Autowired
    private RuleEvaluationService ruleEvaluationService;
    @Autowired
    private FixedRulesService fixedRulesService;
    @Cacheable(value = "recommendationCache", key = "#userId")
    public RecommendationResult getRecommendations(UUID userId) {
        List<DynamicRuleEntity> activeRules = ruleService.getAllActiveRules();
        List<Recommendation> dynamicRecommendations = evaluateDynamicRules(userId, activeRules);
        List<Recommendation> fixedRecommendations = fixedRulesService.getFixedRecommendations(userId);
        List<Recommendation> allRecommendations = new ArrayList<>();
        allRecommendations.addAll(dynamicRecommendations);
        allRecommendations.addAll(fixedRecommendations);
        allRecommendations.sort(Comparator.comparingInt(Recommendation::getPriority).reversed());
        RecommendationResult result = new RecommendationResult();
        result.setUserId(userId);
        result.setRecommendations(allRecommendations);
        result.setTotalRecommendations(allRecommendations.size());
        result.setDynamicRecommendations(dynamicRecommendations.size());
        result.setFixedRecommendations(fixedRecommendations.size());
        result.setGeneratedAt(new Date());
        return result;
    }
    private List<Recommendation> evaluateDynamicRules(UUID userId, List<DynamicRuleEntity> rules) {
        return rules.parallelStream()
                .filter(rule -> ruleEvaluationService.evaluateRule(userId, rule.getRule()))
                .map(this::convertToRecommendation)
                .collect(Collectors.toList());
    }
    private Recommendation convertToRecommendation(DynamicRuleEntity rule) {
        Recommendation recommendation = new Recommendation();
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
        private List<Recommendation> recommendations;
        private int totalRecommendations;
        private int dynamicRecommendations;
        private int fixedRecommendations;
        private Date generatedAt;
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public List<Recommendation> getRecommendations() { return recommendations; }
        public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }
        public int getTotalRecommendations() { return totalRecommendations; }
        public void setTotalRecommendations(int totalRecommendations) { this.totalRecommendations = totalRecommendations; }
        public int getDynamicRecommendations() { return dynamicRecommendations; }
        public void setDynamicRecommendations(int dynamicRecommendations) { this.dynamicRecommendations = dynamicRecommendations; }
        public int getFixedRecommendations() { return fixedRecommendations; }
        public void setFixedRecommendations(int fixedRecommendations) { this.fixedRecommendations = fixedRecommendations; }
        public Date getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(Date generatedAt) { this.generatedAt = generatedAt; }
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
        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getProductText() { return productText; }
        public void setProductText(String productText) { this.productText = productText; }
        public UUID getRuleId() { return ruleId; }
        public void setRuleId(UUID ruleId) { this.ruleId = ruleId; }
        public String getRuleDescription() { return ruleDescription; }
        public void setRuleDescription(String ruleDescription) { this.ruleDescription = ruleDescription; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public boolean isDynamic() { return isDynamic; }
        public void setDynamic(boolean dynamic) { isDynamic = dynamic; }
        public Date getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(Date generatedAt) { this.generatedAt = generatedAt; }
    }
}

@Service
@Transactional(readOnly = true)
class FixedRulesService {
    @Autowired
    private UserProductRepository userProductRepository;
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
        if (!ComparisonOperator.isValid(operator)) {
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

@RestController
@RequestMapping("/api/rules")
class RuleController {
    @Autowired
    private DynamicRuleService ruleService;
    @Autowired
    private ValidationService validationService;
    @PostMapping
    public ResponseEntity<?> createRule(@RequestBody DynamicRuleEntity rule) {
        try {
            DynamicRuleEntity createdRule = ruleService.createRule(rule);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllRules(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String productName,
            Pageable pageable) {
        try {
            if (active != null && active) {
                List<DynamicRuleEntity> activeRules = ruleService.getAllActiveRules();
                return ResponseEntity.ok(Map.of(
                        "data", activeRules,
                        "total", activeRules.size()
                ));
            } else if (productName != null && !productName.trim().isEmpty()) {
                Page<DynamicRuleEntity> rulesPage = ruleService.getRulesPage(pageable);
                return ResponseEntity.ok(Map.of(
                        "data", rulesPage.getContent(),
                        "total", rulesPage.getTotalElements()
                ));
            } else {
                Page<DynamicRuleEntity> rulesPage = ruleService.getRulesPage(pageable);
                return ResponseEntity.ok(Map.of(
                        "data", rulesPage.getContent(),
                        "total", rulesPage.getTotalElements()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getRuleById(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            return ruleService.getRuleById(ruleId)
                    .map(rule -> ResponseEntity.ok(rule))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            Map.of("error", "Правило не найдено", "message", "Правило с ID " + id + " не существует")
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRule(@PathVariable String id,
                                        @RequestBody DynamicRuleEntity updatedRule) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            DynamicRuleEntity rule = ruleService.updateRule(ruleId, updatedRule);
            return ResponseEntity.ok(rule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            ruleService.deleteRule(ruleId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateRule(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            ruleService.activateRule(ruleId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateRule(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            ruleService.deactivateRule(ruleId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @GetMapping("/statistics")
    public ResponseEntity<?> getRuleStatistics() {
        try {
            DynamicRuleService.RuleStatistics stats = ruleService.getRuleStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<?> checkRuleExists(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            boolean exists = ruleService.ruleExists(ruleId);
            if (exists) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

@RestController
@RequestMapping("/api/recommendations")
class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private ValidationService validationService;
    @GetMapping("/{userId}")
    public ResponseEntity<?> getRecommendations(
            @PathVariable String userId,
            @RequestParam(required = false) Boolean debug) {
        try {
            UUID userUuid = validationService.validateAndParseUuid(userId);
            if (Boolean.TRUE.equals(debug)) {
                return ResponseEntity.ok(recommendationService.getRecommendations(userUuid));
            } else {
                return ResponseEntity.ok(recommendationService.getRecommendations(userUuid));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @PostMapping("/batch")
    public ResponseEntity<?> getBatchRecommendations(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<String> userIds = (java.util.List<String>) request.get("userIds");
            if (userIds == null || userIds.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Ошибка валидации", "message", "Список userIds не может быть пустым")
                );
            }
            java.util.List<UUID> userUuids = userIds.stream()
                    .map(validationService::validateAndParseUuid)
                    .toList();
            Map<UUID, RecommendationService.RecommendationResult> results =
                    recommendationService.getBatchRecommendations(userUuids);
            return ResponseEntity.ok(Map.of(
                    "data", results,
                    "total", results.size()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "service", "recommendation-service",
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", "DOWN", "error", e.getMessage())
            );
        }
    }
}