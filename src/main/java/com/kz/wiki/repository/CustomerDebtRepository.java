package com.kz.wiki.repository;

import com.kz.wiki.entity.CustomerDebt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerDebtRepository extends JpaRepository<CustomerDebt, Long> {
    List<CustomerDebt> findByCustomerIdAndTenantId(Long customerId, String tenantId);
    List<CustomerDebt> findByCustomerIdAndTenantIdAndStatus(Long customerId, String tenantId, String status);
    List<CustomerDebt> findBySaleIdAndTenantId(Long saleId, String tenantId);
}

