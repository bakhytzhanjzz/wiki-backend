package com.kz.wiki.repository;

import com.kz.wiki.entity.Sale;
import com.kz.wiki.entity.SaleType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByTenantId(String tenantId);
    
    @EntityGraph(attributePaths = {"items", "items.product", "items.product.category"})
    Optional<Sale> findByIdAndTenantId(Long id, String tenantId);
    
    @Query("SELECT s FROM Sale s WHERE s.tenantId = :tenantId AND " +
           "s.saleTime >= :startDate AND s.saleTime < :endDate AND s.type = :type")
    List<Sale> findByTenantIdAndDateRange(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") SaleType type
    );
    
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.tenantId = :tenantId AND " +
           "s.saleTime >= :startDate AND s.saleTime < :endDate AND s.type = :type")
    Long countByTenantIdAndDateRange(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") SaleType type
    );
    
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.tenantId = :tenantId AND " +
           "s.saleTime >= :startDate AND s.saleTime < :endDate AND s.type = :type")
    java.math.BigDecimal sumTotalAmountByTenantIdAndDateRange(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") SaleType type
    );
}
