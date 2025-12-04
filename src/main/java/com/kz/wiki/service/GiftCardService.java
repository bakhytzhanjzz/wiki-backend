package com.kz.wiki.service;

import com.kz.wiki.entity.GiftCard;
import java.util.List;
import java.util.Optional;

public interface GiftCardService {
    GiftCard create(GiftCard giftCard, String tenantId, Long userId);
    GiftCard update(Long id, GiftCard giftCard, String tenantId);
    Optional<GiftCard> findById(Long id, String tenantId);
    Optional<GiftCard> findByCode(String code, String tenantId);
    List<GiftCard> findAll(String tenantId);
    List<GiftCard> search(String searchTerm, String tenantId);
    List<GiftCard> findByStatus(String status, String tenantId);
    List<GiftCard> findByType(String type, String tenantId);
    void delete(Long id, String tenantId);
    GiftCardValidationResult validate(String code, String tenantId);
    GiftCard use(Long giftCardId, Long saleId, java.math.BigDecimal amount, String tenantId);
    GiftCard refund(Long giftCardId, Long saleId, java.math.BigDecimal amount, String tenantId);
    
    interface GiftCardValidationResult {
        boolean isValid();
        GiftCard getGiftCard();
    }
}




