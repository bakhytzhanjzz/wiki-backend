package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.GiftCard;
import com.kz.wiki.entity.GiftCardUsage;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.GiftCardRepository;
import com.kz.wiki.repository.GiftCardUsageRepository;
import com.kz.wiki.service.GiftCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftCardServiceImpl implements GiftCardService {

    private final GiftCardRepository giftCardRepository;
    private final GiftCardUsageRepository usageRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_GIFT_CARD", entityType = "GIFT_CARD")
    public GiftCard create(GiftCard giftCard, String tenantId, Long userId) {
        giftCard.setTenantId(tenantId);
        giftCard.setIssuedBy(userId);
        
        // Generate code and number if not provided
        if (giftCard.getCode() == null || giftCard.getCode().isEmpty()) {
            giftCard.setCode("GC-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        }
        if (giftCard.getNumber() == null || giftCard.getNumber().isEmpty()) {
            giftCard.setNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        }
        
        giftCard.setRemainingAmount(giftCard.getAmount());
        giftCard.setStatus("active");
        
        GiftCard saved = giftCardRepository.save(giftCard);
        log.info("Gift card created: {} (ID: {}) for tenant: {}", saved.getCode(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_GIFT_CARD", entityType = "GIFT_CARD")
    public GiftCard update(Long id, GiftCard giftCard, String tenantId) {
        GiftCard existing = giftCardRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("GiftCard", "id", id));

        existing.setType(giftCard.getType());
        existing.setExpiresAt(giftCard.getExpiresAt());
        existing.setStatus(giftCard.getStatus());
        existing.setNote(giftCard.getNote());

        GiftCard updated = giftCardRepository.save(existing);
        log.info("Gift card updated: {} (ID: {}) for tenant: {}", updated.getCode(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GiftCard> findById(Long id, String tenantId) {
        return giftCardRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GiftCard> findByCode(String code, String tenantId) {
        return giftCardRepository.findByCodeAndTenantId(code, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCard> findAll(String tenantId) {
        return giftCardRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCard> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return giftCardRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCard> findByStatus(String status, String tenantId) {
        return giftCardRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCard> findByType(String type, String tenantId) {
        return giftCardRepository.findByTenantIdAndType(tenantId, type);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_GIFT_CARD", entityType = "GIFT_CARD")
    public void delete(Long id, String tenantId) {
        GiftCard giftCard = giftCardRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("GiftCard", "id", id));
        
        giftCardRepository.delete(giftCard);
        log.info("Gift card deleted: {} (ID: {}) for tenant: {}", giftCard.getCode(), giftCard.getId(), tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public GiftCardValidationResult validate(String code, String tenantId) {
        Optional<GiftCard> giftCardOpt = giftCardRepository.findByCodeAndTenantId(code, tenantId);
        
        if (giftCardOpt.isEmpty()) {
            return new GiftCardValidationResult() {
                @Override
                public boolean isValid() { return false; }
                @Override
                public GiftCard getGiftCard() { return null; }
            };
        }

        GiftCard giftCard = giftCardOpt.get();
        LocalDateTime now = LocalDateTime.now();

        // Check status
        if (!"active".equals(giftCard.getStatus())) {
            return createInvalidResult();
        }

        // Check expiration
        if (giftCard.getExpiresAt() != null && now.isAfter(giftCard.getExpiresAt())) {
            return createInvalidResult();
        }

        // Check remaining amount
        if (giftCard.getRemainingAmount() == null || giftCard.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return createInvalidResult();
        }

        final GiftCard finalGiftCard = giftCard;
        return new GiftCardValidationResult() {
            @Override
            public boolean isValid() { return true; }
            @Override
            public GiftCard getGiftCard() { return finalGiftCard; }
        };
    }

    @Override
    @Transactional
    @AuditLoggable(action = "USE_GIFT_CARD", entityType = "GIFT_CARD")
    public GiftCard use(Long giftCardId, Long saleId, BigDecimal amount, String tenantId) {
        GiftCard giftCard = giftCardRepository.findByIdAndTenantId(giftCardId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("GiftCard", "id", giftCardId));

        if (!"active".equals(giftCard.getStatus())) {
            throw new BadRequestException("Gift card is not active");
        }

        if (giftCard.getRemainingAmount().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient gift card balance");
        }

        giftCard.setRemainingAmount(giftCard.getRemainingAmount().subtract(amount));
        if (giftCard.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            giftCard.setStatus("used");
        }

        // Record usage
        GiftCardUsage usage = new GiftCardUsage();
        usage.setGiftCardId(giftCardId);
        usage.setSaleId(saleId);
        usage.setAmount(amount);
        usage.setTenantId(tenantId);
        usageRepository.save(usage);

        GiftCard updated = giftCardRepository.save(giftCard);
        log.info("Gift card used: {} (ID: {}), amount: {} for tenant: {}", updated.getCode(), updated.getId(), amount, tenantId);
        return updated;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "REFUND_GIFT_CARD", entityType = "GIFT_CARD")
    public GiftCard refund(Long giftCardId, Long saleId, BigDecimal amount, String tenantId) {
        GiftCard giftCard = giftCardRepository.findByIdAndTenantId(giftCardId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("GiftCard", "id", giftCardId));

        // Remove usage record
        List<GiftCardUsage> usages = usageRepository.findBySaleIdAndTenantId(saleId, tenantId);
        for (GiftCardUsage usage : usages) {
            if (usage.getGiftCardId().equals(giftCardId)) {
                usageRepository.delete(usage);
                break;
            }
        }

        giftCard.setRemainingAmount(giftCard.getRemainingAmount().add(amount));
        if ("used".equals(giftCard.getStatus()) && giftCard.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            giftCard.setStatus("active");
        }

        GiftCard updated = giftCardRepository.save(giftCard);
        log.info("Gift card refunded: {} (ID: {}), amount: {} for tenant: {}", updated.getCode(), updated.getId(), amount, tenantId);
        return updated;
    }

    private GiftCardValidationResult createInvalidResult() {
        return new GiftCardValidationResult() {
            @Override
            public boolean isValid() { return false; }
            @Override
            public GiftCard getGiftCard() { return null; }
        };
    }
}

