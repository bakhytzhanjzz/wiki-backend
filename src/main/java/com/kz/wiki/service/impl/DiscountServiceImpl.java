package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Discount;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.DiscountRepository;
import com.kz.wiki.service.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_DISCOUNT", entityType = "DISCOUNT")
    public Discount create(Discount discount, String tenantId) {
        if (discountRepository.findByCodeAndTenantId(discount.getCode(), tenantId).isPresent()) {
            throw new BadRequestException("Discount with code " + discount.getCode() + " already exists");
        }

        discount.setTenantId(tenantId);
        Discount saved = discountRepository.save(discount);
        log.info("Discount created: {} (ID: {}) for tenant: {}", saved.getCode(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_DISCOUNT", entityType = "DISCOUNT")
    public Discount update(Long id, Discount discount, String tenantId) {
        Discount existing = discountRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", id));

        existing.setName(discount.getName());
        existing.setType(discount.getType());
        existing.setValue(discount.getValue());
        existing.setMinPurchaseAmount(discount.getMinPurchaseAmount());
        existing.setMaxDiscountAmount(discount.getMaxDiscountAmount());
        existing.setApplicableTo(discount.getApplicableTo());
        existing.setApplicableProductIds(discount.getApplicableProductIds());
        existing.setApplicableCategoryIds(discount.getApplicableCategoryIds());
        existing.setStartDate(discount.getStartDate());
        existing.setEndDate(discount.getEndDate());
        existing.setUsageLimit(discount.getUsageLimit());
        existing.setIsActive(discount.getIsActive());

        Discount updated = discountRepository.save(existing);
        log.info("Discount updated: {} (ID: {}) for tenant: {}", updated.getCode(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Discount> findById(Long id, String tenantId) {
        return discountRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Discount> findByCode(String code, String tenantId) {
        return discountRepository.findByCodeAndTenantId(code, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Discount> findAll(String tenantId) {
        return discountRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Discount> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return discountRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Discount> findActive(String tenantId) {
        return discountRepository.findActiveByTenantId(tenantId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Discount> findByType(String type, String tenantId) {
        return discountRepository.findByTenantIdAndType(tenantId, type);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_DISCOUNT", entityType = "DISCOUNT")
    public void delete(Long id, String tenantId) {
        Discount discount = discountRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", id));
        
        discountRepository.delete(discount);
        log.info("Discount deleted: {} (ID: {}) for tenant: {}", discount.getCode(), discount.getId(), tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountValidationResult validate(String code, Long customerId, List<DiscountValidationItem> items, BigDecimal subtotal, String tenantId) {
        Optional<Discount> discountOpt = discountRepository.findByCodeAndTenantId(code, tenantId);
        
        if (discountOpt.isEmpty()) {
            return new DiscountValidationResult() {
                @Override
                public boolean isValid() { return false; }
                @Override
                public Discount getDiscount() { return null; }
                @Override
                public BigDecimal getDiscountAmount() { return BigDecimal.ZERO; }
                @Override
                public BigDecimal getFinalAmount() { return subtotal; }
            };
        }

        Discount discount = discountOpt.get();
        LocalDateTime now = LocalDateTime.now();

        // Check if active
        if (!discount.getIsActive()) {
            return createInvalidResult(subtotal);
        }

        // Check date range
        if (discount.getStartDate() != null && now.isBefore(discount.getStartDate())) {
            return createInvalidResult(subtotal);
        }
        if (discount.getEndDate() != null && now.isAfter(discount.getEndDate())) {
            return createInvalidResult(subtotal);
        }

        // Check usage limit
        if (discount.getUsageLimit() != null && discount.getUsageCount() >= discount.getUsageLimit()) {
            return createInvalidResult(subtotal);
        }

        // Check min purchase amount
        if (discount.getMinPurchaseAmount() != null && subtotal.compareTo(discount.getMinPurchaseAmount()) < 0) {
            return createInvalidResult(subtotal);
        }

        // Calculate discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        if ("percentage".equals(discount.getType())) {
            discountAmount = subtotal.multiply(discount.getValue()).divide(BigDecimal.valueOf(100));
        } else if ("fixed".equals(discount.getType())) {
            discountAmount = discount.getValue();
        }

        // Apply max discount limit
        if (discount.getMaxDiscountAmount() != null && discountAmount.compareTo(discount.getMaxDiscountAmount()) > 0) {
            discountAmount = discount.getMaxDiscountAmount();
        }

        BigDecimal finalAmount = subtotal.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        final Discount finalDiscount = discount;
        final BigDecimal finalDiscountAmount = discountAmount;
        final BigDecimal finalFinalAmount = finalAmount;

        return new DiscountValidationResult() {
            @Override
            public boolean isValid() { return true; }
            @Override
            public Discount getDiscount() { return finalDiscount; }
            @Override
            public BigDecimal getDiscountAmount() { return finalDiscountAmount; }
            @Override
            public BigDecimal getFinalAmount() { return finalFinalAmount; }
        };
    }

    private DiscountValidationResult createInvalidResult(BigDecimal subtotal) {
        return new DiscountValidationResult() {
            @Override
            public boolean isValid() { return false; }
            @Override
            public Discount getDiscount() { return null; }
            @Override
            public BigDecimal getDiscountAmount() { return BigDecimal.ZERO; }
            @Override
            public BigDecimal getFinalAmount() { return subtotal; }
        };
    }
}




