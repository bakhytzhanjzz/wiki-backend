package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateDiscountRequest;
import com.kz.wiki.dto.request.ValidateDiscountRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Discount;
import com.kz.wiki.service.DiscountService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Discount>> createDiscount(@Valid @RequestBody CreateDiscountRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Discount discount = new Discount();
        discount.setCode(request.getCode());
        discount.setName(request.getName());
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        discount.setMinPurchaseAmount(request.getMinPurchaseAmount());
        discount.setMaxDiscountAmount(request.getMaxDiscountAmount());
        discount.setApplicableTo(request.getApplicableTo());
        discount.setApplicableProductIds(request.getApplicableProductIds());
        discount.setApplicableCategoryIds(request.getApplicableCategoryIds());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setUsageLimit(request.getUsageLimit());
        discount.setIsActive(request.getIsActive());
        
        Discount created = discountService.create(discount, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Discount created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Discount>>> getAllDiscounts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String type) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        List<Discount> discounts;
        if (search != null && !search.trim().isEmpty()) {
            discounts = discountService.search(search.trim(), tenantId);
        } else if (isActive != null && isActive) {
            discounts = discountService.findActive(tenantId);
        } else if (type != null && !type.trim().isEmpty()) {
            discounts = discountService.findByType(type.trim(), tenantId);
        } else {
            discounts = discountService.findAll(tenantId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(discounts));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Discount>> getDiscount(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return discountService.findById(id, tenantId)
                .map(discount -> ResponseEntity.ok(ApiResponse.success(discount)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Discount not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Discount>> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody CreateDiscountRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Discount discount = new Discount();
        discount.setName(request.getName());
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        discount.setMinPurchaseAmount(request.getMinPurchaseAmount());
        discount.setMaxDiscountAmount(request.getMaxDiscountAmount());
        discount.setApplicableTo(request.getApplicableTo());
        discount.setApplicableProductIds(request.getApplicableProductIds());
        discount.setApplicableCategoryIds(request.getApplicableCategoryIds());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setUsageLimit(request.getUsageLimit());
        discount.setIsActive(request.getIsActive());
        
        Discount updated = discountService.update(id, discount, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Discount updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDiscount(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        discountService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Discount deleted successfully", null));
    }

    @PostMapping("/validate")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<DiscountService.DiscountValidationResult>> validateDiscount(
            @Valid @RequestBody ValidateDiscountRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        List<DiscountService.DiscountValidationItem> items = new java.util.ArrayList<>();
        for (ValidateDiscountRequest.Item item : request.getItems()) {
            items.add(new DiscountValidationItemImpl(
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPrice()
            ));
        }
        
        DiscountService.DiscountValidationResult result = discountService.validate(
                request.getCode(),
                request.getCustomerId(),
                items,
                request.getSubtotal(),
                tenantId
        );
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    private static class DiscountValidationItemImpl implements DiscountService.DiscountValidationItem {
        private final Long productId;
        private final Integer quantity;
        private final java.math.BigDecimal price;
        
        public DiscountValidationItemImpl(Long productId, Integer quantity, java.math.BigDecimal price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }
        
        @Override
        public Long getProductId() { return productId; }
        @Override
        public Integer getQuantity() { return quantity; }
        @Override
        public java.math.BigDecimal getPrice() { return price; }
    }
}

