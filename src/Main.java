package com.example.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import javax.persistence.*;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// ==================== MAIN APPLICATION ====================
@SpringBootApplication
@EnableCaching
public class RecommendationApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecommendationApplication.class, args);
    }
}

// ==================== DATABASE CONFIGURATION ====================
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.recommendation")
class DatabaseConfig {

    @Primary
    @Bean(name = "knowledgeDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.knowledge")
    public DataSource knowledgeDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "rulesDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.rules")
    public DataSource rulesDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "knowledgeEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean knowledgeEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(knowledgeDataSource());
        em.setPackagesToScan("com.example.recommendation");
        em.setPersistenceUnitName("knowledge");

        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        em.setJpaProperties(properties);

        return em;
    }

    @Bean(name = "rulesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean rulesEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(rulesDataSource());
        em.setPackagesToScan("com.example.recommendation");
        em.setPersistenceUnitName("rules");

        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        em.setJpaProperties(properties);

        return em;
    }

    @Primary
    @Bean(name = "knowledgeTransactionManager")
    public PlatformTransactionManager knowledgeTransactionManager() {
        return new JpaTransactionManager(knowledgeEntityManagerFactory().getObject());
    }

    @Bean(name = "rulesTransactionManager")
    public PlatformTransactionManager rulesTransactionManager() {
        return new JpaTransactionManager(rulesEntityManagerFactory().getObject());
    }
}

// ==================== KNOWLEDGE BASE MODELS ====================
@Entity
@Table(name = "user_transactions")
class UserTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public UserTransaction() {}

    public UserTransaction(UUID userId, String productType, String transactionType,
                           BigDecimal amount, LocalDateTime transactionDate) {
        this.userId = userId;
        this.productType = productType;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}

// ==================== KNOWLEDGE BASE REPOSITORY ====================
@Repository
interface UserKnowledgeRepository extends JpaRepository<UserTransaction, UUID> {

    @Query("SELECT COUNT(DISTINCT t.id) FROM UserTransaction t " +
            "WHERE t.userId = :userId AND t.productType = :productType")
    Long countTransactionsByUserAndProductType(@Param("userId") UUID userId,
                                               @Param("productType") String productType);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM UserTransaction t " +
            "WHERE t.userId = :userId AND t.productType = :productType " +
            "AND t.transactionType = :transactionType")
    BigDecimal sumTransactionsByType(@Param("userId") UUID userId,
                                     @Param("productType") String productType,
                                     @Param("transactionType") String transactionType);

    @Query("SELECT COUNT(DISTINCT t.id) > 0 FROM UserTransaction t " +
            "WHERE t.userId = :userId AND t.productType = :productType")
    boolean existsByUserAndProductType(@Param("userId") UUID userId,
                                       @Param("productType") String productType);
}

// ==================== DYNAMIC RULES MODELS ====================
enum QueryType {
    USER_OF,
    ACTIVE_USER_OF,
    TRANSACTION_SUM_COMPARE,
    TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW
}

@Entity
@Table(name = "dynamic_rules")
class RuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_text", nullable = false, columnDefinition = "TEXT")
    private String productText;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "rule")
    private List<RuleQueryEntity> queries = new ArrayList<>();

    public RuleEntity() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }

    public List<RuleQueryEntity> getQueries() { return queries; }
    public void setQueries(List<RuleQueryEntity> queries) {
        this.queries = queries;
        if (queries != null) {
            queries.forEach(q -> q.setRule(this));
        }
    }
}

@Entity
@Table(name = "rule_queries")
class RuleQueryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "query_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private QueryType queryType;

    @Column(name = "arguments", nullable = false, columnDefinition = "TEXT")
    private String arguments;

    @Column(name = "negate", nullable = false)
    private boolean negate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private RuleEntity rule;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public QueryType getQueryType() { return queryType; }
    public void setQueryType(QueryType queryType) { this.queryType = queryType; }

    public String getArguments() { return arguments; }
    public void setArguments(String arguments) { this.arguments = arguments; }

    public boolean isNegate() { return negate; }
    public void setNegate(boolean negate) { this.negate = negate; }

    public RuleEntity getRule() { return rule; }
    public void setRule(RuleEntity rule) { this.rule = rule; }

    public List<String> getArgumentsAsList() {
        if (arguments == null || arguments.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(arguments.split(","));
    }

    public void setArgumentsFromList(List<String> args) {
        if (args == null || args.isEmpty()) {
            this.arguments = "";
        } else {
            this.arguments = String.join(",", args);
        }
    }
}

// ==================== DYNAMIC RULES REPOSITORY ====================
@Repository
interface DynamicRuleRepository extends JpaRepository<RuleEntity, Long> {
}

// ==================== DTO CLASSES ====================
class DynamicRuleRequest {

    private String product_name;
    private UUID product_id;
    private String product_text;
    private List<RuleQueryDto> rule;

    // Getters and Setters
    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }

    public UUID getProduct_id() { return product_id; }
    public void setProduct_id(UUID product_id) { this.product_id = product_id; }

    public String getProduct_text() { return product_text; }
    public void setProduct_text(String product_text) { this.product_text = product_text; }

    public List<RuleQueryDto> getRule() { return rule; }
    public void setRule(List<RuleQueryDto> rule) { this.rule = rule; }
}

class RuleQueryDto {

    private final String query;
    private final List<String> arguments;
    private final boolean negate;

    @JsonCreator
    public RuleQueryDto(
            @JsonProperty("query") String query,
            @JsonProperty("arguments") List<String> arguments,
            @JsonProperty("negate") boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    // Getters
    public String getQuery() { return query; }
    public List<String> getArguments() { return arguments; }
    public boolean isNegate() { return negate; }
}

class DynamicRuleResponse {

    private Long id;
    private String product_name;
    private UUID product_id;
    private String product_text;
    private List<RuleQueryDto> rule;

    // Constructors
    public DynamicRuleResponse() {}

    public DynamicRuleResponse(Long id, String product_name, UUID product_id,
                               String product_text, List<RuleQueryDto> rule) {
        this.id = id;
        this.product_name = product_name;
        this.product_id = product_id;
        this.product_text = product_text;
        this.rule = rule;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }

    public UUID getProduct_id() { return product_id; }
    public void setProduct_id(UUID product_id) { this.product_id = product_id; }

    public String getProduct_text() { return product_text; }
    public void setProduct_text(String product_text) { this.product_text = product_text; }

    public List<RuleQueryDto> getRule() { return rule; }
    public void setRule(List<RuleQueryDto> rule) { this.rule = rule; }
}

class RulesListResponse {

    private List<DynamicRuleResponse> data;

    public RulesListResponse() {}

    public RulesListResponse(List<DynamicRuleResponse> data) {
        this.data = data;
    }

    public List<DynamicRuleResponse> getData() { return data; }
    public void setData(List<DynamicRuleResponse> data) { this.data = data; }
}

class ProductRecommendation {

    private UUID productId;
    private String productName;
    private String recommendationText;

    public ProductRecommendation() {}

    public ProductRecommendation(UUID productId, String productName, String recommendationText) {
        this.productId = productId;
        this.productName = productName;
        this.recommendationText = recommendationText;
    }

    // Getters and Setters
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getRecommendationText() { return recommendationText; }
    public void setRecommendationText(String recommendationText) { this.recommendationText = recommendationText; }
}

// ==================== DYNAMIC RULES SERVICE ====================
@Service
class DynamicRuleService {

    private final DynamicRuleRepository ruleRepository;

    public DynamicRuleService(DynamicRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Transactional(transactionManager = "rulesTransactionManager")
    public DynamicRuleResponse createRule(DynamicRuleRequest request) {
        RuleEntity entity = new RuleEntity();
        entity.setProductName(request.getProduct_name());
        entity.setProductId(request.getProduct_id().toString());
        entity.setProductText(request.getProduct_text());

        List<RuleQueryEntity> queries = request.getRule().stream()
                .map(dto -> {
                    RuleQueryEntity queryEntity = new RuleQueryEntity();
                    queryEntity.setQueryType(QueryType.valueOf(dto.getQuery()));
                    queryEntity.setArgumentsFromList(dto.getArguments());
                    queryEntity.setNegate(dto.isNegate());
                    queryEntity.setRule(entity);
                    return queryEntity;
                })
                .collect(Collectors.toList());

        entity.setQueries(queries);

        RuleEntity saved = ruleRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional(readOnly = true, transactionManager = "rulesTransactionManager")
    public List<DynamicRuleResponse> getAllRules() {
        return ruleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(transactionManager = "rulesTransactionManager")
    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }

    private DynamicRuleResponse toResponse(RuleEntity entity) {
        List<RuleQueryDto> queries = entity.getQueries().stream()
                .map(query -> new RuleQueryDto(
                        query.getQueryType().name(),
                        query.getArgumentsAsList(),
                        query.isNegate()
                ))
                .collect(Collectors.toList());

        return new DynamicRuleResponse(
                entity.getId(),
                entity.getProductName(),
                UUID.fromString(entity.getProductId()),
                entity.getProductText(),
                queries
        );
    }
}

// ==================== ENHANCED RECOMMENDATION SERVICE ====================
@Service
class EnhancedRecommendationService {

    private final UserKnowledgeRepository userKnowledgeRepository;
    private final DynamicRuleService dynamicRuleService;

    // Кеши с использованием Caffeine
    private final Cache<String, Boolean> userOfCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .maximumSize(10000)
            .build();

    private final Cache<String, Boolean> activeUserOfCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .maximumSize(10000)
            .build();

    private final Cache<String, BigDecimal> transactionSumCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .maximumSize(10000)
            .build();

    public EnhancedRecommendationService(UserKnowledgeRepository userKnowledgeRepository,
                                         DynamicRuleService dynamicRuleService) {
        this.userKnowledgeRepository = userKnowledgeRepository;
        this.dynamicRuleService = dynamicRuleService;
    }

    public List<ProductRecommendation> getRecommendations(UUID userId) {
        List<ProductRecommendation> recommendations = new ArrayList<>();

        // Старые фиксированные правила (пример)
        recommendations.addAll(getFixedRecommendations(userId));

        // Добавляем рекомендации из динамических правил
        List<DynamicRuleResponse> dynamicRules = dynamicRuleService.getAllRules();

        for (DynamicRuleResponse rule : dynamicRules) {
            if (evaluateRule(userId, rule)) {
                recommendations.add(new ProductRecommendation(
                        rule.getProduct_id(),
                        rule.getProduct_name(),
                        rule.getProduct_text()
                ));
            }
        }

        return recommendations;
    }

    private List<ProductRecommendation> getFixedRecommendations(UUID userId) {
        // Пример старых фиксированных правил
        List<ProductRecommendation> fixed = new ArrayList<>();

        // Пример правила: если пользователь имеет дебетовые транзакции > 50000
        BigDecimal debitDepositSum = userKnowledgeRepository.sumTransactionsByType(
                userId, "DEBIT", "DEPOSIT");

        if (debitDepositSum.compareTo(new BigDecimal("50000")) > 0) {
            fixed.add(new ProductRecommendation(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    "Премиальная карта",
                    "Вам доступна премиальная карта с повышенным кэшбэком"
            ));
        }

        return fixed;
    }

    private boolean evaluateRule(UUID userId, DynamicRuleResponse rule) {
        for (RuleQueryDto query : rule.getRule()) {
            if (!evaluateQuery(userId, query)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateQuery(UUID userId, RuleQueryDto query) {
        boolean result;

        switch (query.getQuery()) {
            case "USER_OF":
                result = checkUserOf(userId, query);
                break;
            case "ACTIVE_USER_OF":
                result = checkActiveUserOf(userId, query);
                break;
            case "TRANSACTION_SUM_COMPARE":
                result = checkTransactionSumCompare(userId, query);
                break;
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                result = checkTransactionSumCompareDepositWithdraw(userId, query);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type: " + query.getQuery());
        }

        return query.isNegate() != result;
    }

    private boolean checkUserOf(UUID userId, RuleQueryDto query) {
        String productType = query.getArguments().get(0);
        String cacheKey = userId + "_USER_OF_" + productType;

        return userOfCache.get(cacheKey, key ->
                userKnowledgeRepository.existsByUserAndProductType(userId, productType)
        );
    }

    private boolean checkActiveUserOf(UUID userId, RuleQueryDto query) {
        String productType = query.getArguments().get(0);
        String cacheKey = userId + "_ACTIVE_USER_OF_" + productType;

        return activeUserOfCache.get(cacheKey, key -> {
            Long count = userKnowledgeRepository.countTransactionsByUserAndProductType(userId, productType);
            return count != null && count >= 5;
        });
    }

    private boolean checkTransactionSumCompare(UUID userId, RuleQueryDto query) {
        List<String> args = query.getArguments();
        String productType = args.get(0);
        String transactionType = args.get(1);
        String operator = args.get(2);
        BigDecimal constant = new BigDecimal(args.get(3));

        String cacheKey = userId + "_SUM_" + productType + "_" + transactionType;
        BigDecimal sum = transactionSumCache.get(cacheKey, key ->
                userKnowledgeRepository.sumTransactionsByType(userId, productType, transactionType)
        );

        return compareValues(sum, operator, constant);
    }

    private boolean checkTransactionSumCompareDepositWithdraw(UUID userId, RuleQueryDto query) {
        List<String> args = query.getArguments();
        String productType = args.get(0);
        String operator = args.get(1);

        String depositKey = userId + "_SUM_" + productType + "_DEPOSIT";
        String withdrawKey = userId + "_SUM_" + productType + "_WITHDRAW";

        BigDecimal depositSum = transactionSumCache.get(depositKey, key ->
                userKnowledgeRepository.sumTransactionsByType(userId, productType, "DEPOSIT")
        );

        BigDecimal withdrawSum = transactionSumCache.get(withdrawKey, key ->
                userKnowledgeRepository.sumTransactionsByType(userId, productType, "WITHDRAW")
        );

        return compareValues(depositSum, operator, withdrawSum);
    }

    private boolean compareValues(BigDecimal value1, String operator, BigDecimal value2) {
        switch (operator) {
            case ">": return value1.compareTo(value2) > 0;
            case "<": return value1.compareTo(value2) < 0;
            case "=": return value1.compareTo(value2) == 0;
            case ">=": return value1.compareTo(value2) >= 0;
            case "<=": return value1.compareTo(value2) <= 0;
            default: throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}

// ==================== CONTROLLERS ====================
@RestController
@RequestMapping("/rule")
class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    @PostMapping
    public ResponseEntity<DynamicRuleResponse> createRule(
            @Valid @RequestBody DynamicRuleRequest request) {
        DynamicRuleResponse response = dynamicRuleService.createRule(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RulesListResponse> getAllRules() {
        List<DynamicRuleResponse> rules = dynamicRuleService.getAllRules();
        return ResponseEntity.ok(new RulesListResponse(rules));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        dynamicRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@RestController
@RequestMapping("/recommendation")
class RecommendationController {

    private final EnhancedRecommendationService recommendationService;

    public RecommendationController(EnhancedRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public List<ProductRecommendation> getRecommendations(@PathVariable UUID userId) {
        return recommendationService.getRecommendations(userId);
    }
}

// ==================== DATA INITIALIZATION ====================
@Component
class DataInitializer implements CommandLineRunner {

    private final UserKnowledgeRepository userRepository;
    private final DynamicRuleService ruleService;

    public DataInitializer(UserKnowledgeRepository userRepository,
                           DynamicRuleService ruleService) {
        this.userRepository = userRepository;
        this.ruleService = ruleService;
    }

    @Override
    public void run(String... args) {
        // Инициализация тестовых данных транзакций
        UUID testUserId = UUID.fromString("12345678-1234-1234-1234-123456789012");

        // Очистка старых данных
        userRepository.deleteAll();

        // Создание тестовых транзакций
        List<UserTransaction> transactions = Arrays.asList(
                new UserTransaction(testUserId, "DEBIT", "DEPOSIT",
                        new BigDecimal("150000"), LocalDateTime.now().minusDays(10)),
                new UserTransaction(testUserId, "DEBIT", "DEPOSIT",
                        new BigDecimal("50000"), LocalDateTime.now().minusDays(5)),
                new UserTransaction(testUserId, "DEBIT", "WITHDRAW",
                        new BigDecimal("30000"), LocalDateTime.now().minusDays(3)),
                new UserTransaction(testUserId, "DEBIT", "WITHDRAW",
                        new BigDecimal("20000"), LocalDateTime.now().minusDays(2)),
                new UserTransaction(testUserId, "CREDIT", "DEPOSIT",
                        new BigDecimal("100000"), LocalDateTime.now().minusDays(1))
        );

        userRepository.saveAll(transactions);

        // Создание динамического правила для тестирования
        DynamicRuleRequest ruleRequest = new DynamicRuleRequest();
        ruleRequest.setProduct_name("Простой кредит");
        ruleRequest.setProduct_id(UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"));
        ruleRequest.setProduct_text("Рекомендуем простой кредит на выгодных условиях");

        List<RuleQueryDto> queries = Arrays.asList(
                new RuleQueryDto("USER_OF", Arrays.asList("CREDIT"), true),
                new RuleQueryDto("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",
                        Arrays.asList("DEBIT", ">"), false),
                new RuleQueryDto("TRANSACTION_SUM_COMPARE",
                        Arrays.asList("DEBIT", "DEPOSIT", ">", "100000"), false)
        );

        ruleRequest.setRule(queries);

        try {
            ruleService.createRule(ruleRequest);
            System.out.println("Test data initialized successfully!");
        } catch (Exception e) {
            System.out.println("Rule already exists or error: " + e.getMessage());
        }
    }
}

// ==================== APPLICATION.PROPERTIES CONFIG ====================
@Configuration
class AppPropertiesConfig {

    @Bean
    @Primary
    public org.springframework.boot.autoconfigure.jdbc.DataSourceProperties dataSourceProperties() {
        return new org.springframework.boot.autoconfigure.jdbc.DataSourceProperties();
    }
}