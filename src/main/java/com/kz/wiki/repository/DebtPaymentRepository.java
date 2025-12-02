package com.kz.wiki.repository;

import com.kz.wiki.entity.DebtPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DebtPaymentRepository extends JpaRepository<DebtPayment, Long> {
    List<DebtPayment> findByCustomerIdAndTenantId(Long customerId, String tenantId);
    List<DebtPayment> findByDebtIdAndTenantId(Long debtId, String tenantId);
    List<DebtPayment> findBySaleIdAndTenantId(Long saleId, String tenantId);
    
    @Query("SELECT COALESCE(SUM(dp.amount), 0) FROM DebtPayment dp WHERE dp.customerId = :customerId AND dp.tenantId = :tenantId")
    BigDecimal getTotalPaymentsByCustomerId(@Param("customerId") Long customerId, @Param("tenantId") String tenantId);
}

