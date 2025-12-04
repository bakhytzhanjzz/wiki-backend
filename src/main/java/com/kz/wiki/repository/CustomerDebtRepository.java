package com.kz.wiki.repository;

import com.kz.wiki.entity.CustomerDebt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDebtRepository extends JpaRepository<CustomerDebt, Long> {
    List<CustomerDebt> findByCustomerIdAndTenantId(Long customerId, String tenantId);
    List<CustomerDebt> findByCustomerIdAndTenantIdAndStatus(Long customerId, String tenantId, String status);
    List<CustomerDebt> findBySaleIdAndTenantId(Long saleId, String tenantId);
    Optional<CustomerDebt> findByIdAndTenantId(Long id, String tenantId);
    
    @Query("SELECT d FROM CustomerDebt d WHERE d.tenantId = :tenantId AND " +
           "(:search IS NULL OR " +
           "CAST(d.id AS string) LIKE CONCAT('%', :search, '%') OR " +
           "CAST(d.customerId AS string) LIKE CONCAT('%', :search, '%')) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:storeId IS NULL OR d.storeId = :storeId) AND " +
           "(:paymentType IS NULL OR d.paymentType = :paymentType) AND " +
           "(:repaymentAmountFrom IS NULL OR d.paidAmount >= :repaymentAmountFrom) AND " +
           "(:repaymentAmountTo IS NULL OR d.paidAmount <= :repaymentAmountTo) AND " +
           "(:clientId IS NULL OR d.customerId = :clientId) AND " +
           "(:userId IS NULL OR d.userId = :userId) AND " +
           "(:issueDateFrom IS NULL OR d.issueDate >= :issueDateFrom) AND " +
           "(:issueDateTo IS NULL OR d.issueDate <= :issueDateTo)")
    Page<CustomerDebt> findByFilters(@Param("tenantId") String tenantId,
                                      @Param("search") String search,
                                      @Param("status") String status,
                                      @Param("storeId") Long storeId,
                                      @Param("paymentType") String paymentType,
                                      @Param("repaymentAmountFrom") java.math.BigDecimal repaymentAmountFrom,
                                      @Param("repaymentAmountTo") java.math.BigDecimal repaymentAmountTo,
                                      @Param("clientId") Long clientId,
                                      @Param("userId") Long userId,
                                      @Param("issueDateFrom") LocalDateTime issueDateFrom,
                                      @Param("issueDateTo") LocalDateTime issueDateTo,
                                      Pageable pageable);
}



