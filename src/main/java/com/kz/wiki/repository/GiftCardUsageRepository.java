package com.kz.wiki.repository;

import com.kz.wiki.entity.GiftCardUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GiftCardUsageRepository extends JpaRepository<GiftCardUsage, Long> {
    List<GiftCardUsage> findByGiftCardIdAndTenantId(Long giftCardId, String tenantId);
    List<GiftCardUsage> findBySaleIdAndTenantId(Long saleId, String tenantId);
    
    @Query("SELECT COALESCE(SUM(gcu.amount), 0) FROM GiftCardUsage gcu WHERE gcu.giftCardId = :giftCardId AND gcu.tenantId = :tenantId")
    BigDecimal getTotalUsedByGiftCardId(@Param("giftCardId") Long giftCardId, @Param("tenantId") String tenantId);
}


