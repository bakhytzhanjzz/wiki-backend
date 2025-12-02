package com.kz.wiki.service;

import com.kz.wiki.entity.Discount;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DiscountService {
    Discount create(Discount discount, String tenantId);
    Discount update(Long id, Discount discount, String tenantId);
    Optional<Discount> findById(Long id, String tenantId);
    Optional<Discount> findByCode(String code, String tenantId);
    List<Discount> findAll(String tenantId);
    List<Discount> search(String searchTerm, String tenantId);
    List<Discount> findActive(String tenantId);
    List<Discount> findByType(String type, String tenantId);
    void delete(Long id, String tenantId);
    DiscountValidationResult validate(String code, Long customerId, List<DiscountValidationItem> items, BigDecimal subtotal, String tenantId);
    
    interface DiscountValidationItem {
        Long getProductId();
        Integer getQuantity();
        BigDecimal getPrice();
    }
    
    interface DiscountValidationResult {
        boolean isValid();
        Discount getDiscount();
        BigDecimal getDiscountAmount();
        BigDecimal getFinalAmount();
    }
}

