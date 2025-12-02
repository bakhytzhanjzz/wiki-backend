package com.kz.wiki.repository;

import com.kz.wiki.entity.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {
    Optional<GiftCard> findByIdAndTenantId(Long id, String tenantId);
    List<GiftCard> findByTenantId(String tenantId);
    Optional<GiftCard> findByCodeAndTenantId(String code, String tenantId);
    Optional<GiftCard> findByNumberAndTenantId(String number, String tenantId);
    
    @Query("SELECT gc FROM GiftCard gc WHERE gc.tenantId = :tenantId AND " +
           "(LOWER(gc.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(gc.number) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<GiftCard> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    List<GiftCard> findByTenantIdAndStatus(String tenantId, String status);
    List<GiftCard> findByTenantIdAndType(String tenantId, String type);
    List<GiftCard> findByTenantIdAndStoreId(String tenantId, Long storeId);
    
    @Query("SELECT gc FROM GiftCard gc WHERE gc.tenantId = :tenantId AND " +
           "gc.status = 'active' AND " +
           "(gc.expiresAt IS NULL OR gc.expiresAt >= :now) AND " +
           "gc.remainingAmount > 0")
    List<GiftCard> findActiveByTenantId(@Param("tenantId") String tenantId, @Param("now") LocalDateTime now);
}

