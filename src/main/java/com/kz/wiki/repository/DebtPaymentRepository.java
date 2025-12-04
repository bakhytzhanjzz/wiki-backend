package com.kz.wiki.repository;

import com.kz.wiki.entity.DebtPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DebtPaymentRepository extends JpaRepository<DebtPayment, Long> {
    List<DebtPayment> findByCustomerIdAndTenantId(Long customerId, String tenantId);
    List<DebtPayment> findByDebtIdAndTenantId(Long debtId, String tenantId);
    List<DebtPayment> findBySaleIdAndTenantId(Long saleId, String tenantId);
    Optional<DebtPayment> findByIdAndTenantId(Long id, String tenantId);
    
    @Query("SELECT COALESCE(SUM(dp.amount), 0) FROM DebtPayment dp WHERE dp.customerId = :customerId AND dp.tenantId = :tenantId")
    BigDecimal getTotalPaymentsByCustomerId(@Param("customerId") Long customerId, @Param("tenantId") String tenantId);
    
    @Query("SELECT dp FROM DebtPayment dp WHERE dp.tenantId = :tenantId AND " +
           "(:search IS NULL OR " +
           "CAST(dp.id AS string) LIKE CONCAT('%', :search, '%') OR " +
           "CAST(dp.customerId AS string) LIKE CONCAT('%', :search, '%')) AND " +
           "(:storeId IS NULL OR EXISTS (SELECT s FROM Sale s WHERE s.id = dp.saleId AND s.storeId = :storeId)) AND " +
           "(:paymentType IS NULL OR dp.paymentMethod = :paymentType) AND " +
           "(:repaymentAmountFrom IS NULL OR dp.amount >= :repaymentAmountFrom) AND " +
           "(:repaymentAmountTo IS NULL OR dp.amount <= :repaymentAmountTo) AND " +
           "(:clientId IS NULL OR dp.customerId = :clientId) AND " +
           "(:userId IS NULL OR dp.userId = :userId) AND " +
           "(:repaymentDateFrom IS NULL OR dp.paymentDate >= :repaymentDateFrom) AND " +
           "(:repaymentDateTo IS NULL OR dp.paymentDate <= :repaymentDateTo)")
    Page<DebtPayment> findByFilters(@Param("tenantId") String tenantId,
                                     @Param("search") String search,
                                     @Param("storeId") Long storeId,
                                     @Param("paymentType") String paymentType,
                                     @Param("repaymentAmountFrom") BigDecimal repaymentAmountFrom,
                                     @Param("repaymentAmountTo") BigDecimal repaymentAmountTo,
                                     @Param("clientId") Long clientId,
                                     @Param("userId") Long userId,
                                     @Param("repaymentDateFrom") LocalDateTime repaymentDateFrom,
                                     @Param("repaymentDateTo") LocalDateTime repaymentDateTo,
                                     Pageable pageable);
}



