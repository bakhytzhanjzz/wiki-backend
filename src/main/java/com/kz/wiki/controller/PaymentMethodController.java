package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreatePaymentMethodRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.PaymentMethod;
import com.kz.wiki.service.PaymentMethodService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentMethod>> createPaymentMethod(@Valid @RequestBody CreatePaymentMethodRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setCode(request.getCode());
        paymentMethod.setName(request.getName());
        paymentMethod.setIcon(request.getIcon());
        paymentMethod.setIsActive(request.getIsActive());
        paymentMethod.setSortOrder(request.getSortOrder());
        
        PaymentMethod created = paymentMethodService.create(paymentMethod, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment method created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentMethod>>> getAllPaymentMethods(
            @RequestParam(required = false) Boolean active) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        List<PaymentMethod> methods;
        if (active != null && active) {
            methods = paymentMethodService.findActive(tenantId);
        } else {
            methods = paymentMethodService.findAll(tenantId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(methods));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentMethod>> getPaymentMethod(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return paymentMethodService.findById(id, tenantId)
                .map(method -> ResponseEntity.ok(ApiResponse.success(method)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Payment method not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentMethod>> updatePaymentMethod(
            @PathVariable Long id,
            @Valid @RequestBody CreatePaymentMethodRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName(request.getName());
        paymentMethod.setIcon(request.getIcon());
        paymentMethod.setIsActive(request.getIsActive());
        paymentMethod.setSortOrder(request.getSortOrder());
        
        PaymentMethod updated = paymentMethodService.update(id, paymentMethod, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Payment method updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePaymentMethod(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        paymentMethodService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Payment method deleted successfully", null));
    }
}


