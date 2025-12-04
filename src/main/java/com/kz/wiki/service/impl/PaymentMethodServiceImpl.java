package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.PaymentMethod;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.PaymentMethodRepository;
import com.kz.wiki.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_PAYMENT_METHOD", entityType = "PAYMENT_METHOD")
    public PaymentMethod create(PaymentMethod paymentMethod, String tenantId) {
        if (paymentMethodRepository.findByCodeAndTenantId(paymentMethod.getCode(), tenantId).isPresent()) {
            throw new BadRequestException("Payment method with code " + paymentMethod.getCode() + " already exists");
        }

        paymentMethod.setTenantId(tenantId);
        PaymentMethod saved = paymentMethodRepository.save(paymentMethod);
        log.info("Payment method created: {} (ID: {}) for tenant: {}", saved.getName(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_PAYMENT_METHOD", entityType = "PAYMENT_METHOD")
    public PaymentMethod update(Long id, PaymentMethod paymentMethod, String tenantId) {
        PaymentMethod existing = paymentMethodRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", id));

        existing.setName(paymentMethod.getName());
        existing.setIcon(paymentMethod.getIcon());
        existing.setIsActive(paymentMethod.getIsActive());
        existing.setSortOrder(paymentMethod.getSortOrder());

        PaymentMethod updated = paymentMethodRepository.save(existing);
        log.info("Payment method updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentMethod> findById(Long id, String tenantId) {
        return paymentMethodRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethod> findAll(String tenantId) {
        return paymentMethodRepository.findByTenantIdOrderBySortOrderAsc(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethod> findActive(String tenantId) {
        return paymentMethodRepository.findByTenantIdAndIsActive(tenantId, true);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_PAYMENT_METHOD", entityType = "PAYMENT_METHOD")
    public void delete(Long id, String tenantId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", id));
        
        paymentMethodRepository.delete(paymentMethod);
        log.info("Payment method deleted: {} (ID: {}) for tenant: {}", paymentMethod.getName(), paymentMethod.getId(), tenantId);
    }
}




