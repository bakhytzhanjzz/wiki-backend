package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateRepricingRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Repricing;
import com.kz.wiki.entity.RepricingItem;
import com.kz.wiki.entity.Product;
import com.kz.wiki.service.RepricingService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/repricing")
@RequiredArgsConstructor
public class RepricingController {

    private final RepricingService repricingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Repricing>> createRepricing(@Valid @RequestBody CreateRepricingRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();

        Repricing repricing = new Repricing();
        repricing.setName(request.getName());
        repricing.setStoreId(request.getStoreId());
        repricing.setType(request.getType());

        for (CreateRepricingRequest.RepricingItemRequest itemRequest : request.getItems()) {
            RepricingItem item = new RepricingItem();
            Product product = new Product();
            product.setId(itemRequest.getProductId());
            item.setProduct(product);
            item.setNewPrice(itemRequest.getNewPrice());
            repricing.getItems().add(item);
        }

        Repricing created = repricingService.create(repricing, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Repricing created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Repricing>>> getAllRepricings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long userId) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        List<Repricing> repricings;
        if (search != null && !search.trim().isEmpty()) {
            repricings = repricingService.search(search.trim(), tenantId);
        } else if (storeId != null) {
            repricings = repricingService.findByStoreId(storeId, tenantId);
        } else if (type != null && !type.trim().isEmpty()) {
            repricings = repricingService.findByType(type.trim(), tenantId);
        } else if (date != null) {
            repricings = repricingService.findByDate(date, tenantId);
        } else {
            repricings = repricingService.findAll(tenantId);
        }

        return ResponseEntity.ok(ApiResponse.success(repricings));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Repricing>> getRepricing(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return repricingService.findById(id, tenantId)
                .map(repricing -> ResponseEntity.ok(ApiResponse.success(repricing)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Repricing not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Repricing>> updateRepricing(
            @PathVariable Long id,
            @Valid @RequestBody CreateRepricingRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        Repricing repricing = new Repricing();
        repricing.setName(request.getName());
        repricing.setStoreId(request.getStoreId());
        repricing.setType(request.getType());

        Repricing updated = repricingService.update(id, repricing, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Repricing updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRepricing(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        repricingService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Repricing deleted successfully", null));
    }
}

