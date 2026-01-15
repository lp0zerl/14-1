package repository;

import entity.DynamicRuleEntity;

import java.util.List;
import java.util.UUID;

@Repository
interface DynamicRuleRepository extends JpaRepository<DynamicRuleEntity, UUID> {
    @Query("SELECT r FROM DynamicRuleEntity r WHERE r.isActive = true ORDER BY r.priority DESC, r.createdAt DESC")
    List<DynamicRuleEntity> findAllActive();

    @Query("SELECT r FROM DynamicRuleEntity r WHERE r.productId = :productId AND r.isActive = true")
    List<DynamicRuleEntity> findActiveByProductId(@Param("productId") UUID productId);

    @Query("SELECT COUNT(r) FROM DynamicRuleEntity r WHERE r.isActive = true")
    long countActive();
}
