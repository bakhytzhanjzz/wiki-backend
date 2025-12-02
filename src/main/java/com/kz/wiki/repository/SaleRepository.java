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
    
    List<Sale> findByTenantIdAndStoreId(String tenantId, Long storeId);
    List<Sale> findByTenantIdAndPaymentMethod(String tenantId, String paymentMethod);
    List<Sale> findByTenantIdAndSellerId(String tenantId, Long sellerId);
    List<Sale> findByTenantIdAndCustomerId(String tenantId, Long customerId);
    List<Sale> findByTenantIdAndStatus(String tenantId, String status);
    List<Sale> findByTenantIdAndType(String tenantId, SaleType type);
    
    @Query("SELECT s FROM Sale s WHERE s.tenantId = :tenantId AND " +
           "s.totalAmount >= :minAmount AND s.totalAmount <= :maxAmount")
    List<Sale> findByTenantIdAndAmountRange(@Param("tenantId") String tenantId,
                                             @Param("minAmount") java.math.BigDecimal minAmount,
                                             @Param("maxAmount") java.math.BigDecimal maxAmount);
    
    @Query("SELECT s FROM Sale s WHERE s.tenantId = :tenantId AND " +
           "(LOWER(s.transactionNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.receiptNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(s.id AS string) LIKE CONCAT('%', :searchTerm, '%'))")
    List<Sale> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT s FROM Sale s WHERE s.tenantId = :tenantId AND " +
           "DATE(s.saleTime) BETWEEN :startDate AND :endDate")
    List<Sale> findByTenantIdAndDateRangeOnly(@Param("tenantId") String tenantId,
                                               @Param("startDate") java.time.LocalDate startDate,
                                               @Param("endDate") java.time.LocalDate endDate);
    
    Optional<Sale> findByTransactionNumberAndTenantId(String transactionNumber, String tenantId);
    Optional<Sale> findByReceiptNumberAndTenantId(String receiptNumber, String tenantId);
}
