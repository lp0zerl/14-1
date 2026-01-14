package repository;

import entity.TransactionEntity;

import java.math.BigDecimal;
import java.util.UUID;

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
