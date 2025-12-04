package com.kz.wiki.repository;

import com.kz.wiki.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByIdAndTenantId(Long id, String tenantId);
    List<PaymentMethod> findByTenantId(String tenantId);
    Optional<PaymentMethod> findByCodeAndTenantId(String code, String tenantId);
    List<PaymentMethod> findByTenantIdAndIsActive(String tenantId, Boolean isActive);
    List<PaymentMethod> findByTenantIdOrderBySortOrderAsc(String tenantId);
}



