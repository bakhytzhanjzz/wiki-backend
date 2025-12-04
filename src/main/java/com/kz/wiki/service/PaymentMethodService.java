package com.kz.wiki.service;

import com.kz.wiki.entity.PaymentMethod;
import java.util.List;
import java.util.Optional;

public interface PaymentMethodService {
    PaymentMethod create(PaymentMethod paymentMethod, String tenantId);
    PaymentMethod update(Long id, PaymentMethod paymentMethod, String tenantId);
    Optional<PaymentMethod> findById(Long id, String tenantId);
    List<PaymentMethod> findAll(String tenantId);
    List<PaymentMethod> findActive(String tenantId);
    void delete(Long id, String tenantId);
}



