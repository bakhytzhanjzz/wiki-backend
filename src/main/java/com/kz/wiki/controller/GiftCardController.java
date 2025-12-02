package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateGiftCardRequest;
import com.kz.wiki.dto.request.UseGiftCardRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.GiftCard;
import com.kz.wiki.service.GiftCardService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gift-cards")
@RequiredArgsConstructor
public class GiftCardController {

    private final GiftCardService giftCardService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GiftCard>> createGiftCard(@Valid @RequestBody CreateGiftCardRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        GiftCard giftCard = new GiftCard();
        giftCard.setType(request.getType());
        giftCard.setAmount(request.getAmount());
        giftCard.setExpiresAt(request.getExpiresAt());
        giftCard.setStoreId(request.getStoreId());
        giftCard.setNote(request.getNote());
        
        GiftCard created = giftCardService.create(giftCard, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Gift card created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<GiftCard>>> getAllGiftCards(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long storeId) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        List<GiftCard> giftCards;
        if (search != null && !search.trim().isEmpty()) {
            giftCards = giftCardService.search(search.trim(), tenantId);
        } else if (status != null && !status.trim().isEmpty()) {
            giftCards = giftCardService.findByStatus(status.trim(), tenantId);
        } else if (type != null && !type.trim().isEmpty()) {
            giftCards = giftCardService.findByType(type.trim(), tenantId);
        } else {
            giftCards = giftCardService.findAll(tenantId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(giftCards));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GiftCard>> getGiftCard(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return giftCardService.findById(id, tenantId)
                .map(giftCard -> ResponseEntity.ok(ApiResponse.success(giftCard)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Gift card not found")));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<GiftCard>> getGiftCardByCode(@PathVariable String code) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return giftCardService.findByCode(code, tenantId)
                .map(giftCard -> ResponseEntity.ok(ApiResponse.success(giftCard)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Gift card not found")));
    }

    @PostMapping("/validate")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<GiftCardService.GiftCardValidationResult>> validateGiftCard(
            @Valid @RequestBody com.kz.wiki.dto.request.ValidateGiftCardRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        GiftCardService.GiftCardValidationResult result = giftCardService.validate(request.getCode(), tenantId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{id}/use")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<GiftCard>> useGiftCard(
            @PathVariable Long id,
            @Valid @RequestBody UseGiftCardRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        GiftCard used = giftCardService.use(id, request.getSaleId(), request.getAmount(), tenantId);
        return ResponseEntity.ok(ApiResponse.success("Gift card used successfully", used));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GiftCard>> refundGiftCard(
            @PathVariable Long id,
            @Valid @RequestBody UseGiftCardRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        GiftCard refunded = giftCardService.refund(id, request.getSaleId(), request.getAmount(), tenantId);
        return ResponseEntity.ok(ApiResponse.success("Gift card refunded successfully", refunded));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GiftCard>> updateGiftCard(
            @PathVariable Long id,
            @Valid @RequestBody CreateGiftCardRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        GiftCard giftCard = new GiftCard();
        giftCard.setType(request.getType());
        giftCard.setExpiresAt(request.getExpiresAt());
        giftCard.setStatus(request.getStatus());
        giftCard.setNote(request.getNote());
        
        GiftCard updated = giftCardService.update(id, giftCard, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Gift card updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteGiftCard(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        giftCardService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Gift card deleted successfully", null));
    }
}

