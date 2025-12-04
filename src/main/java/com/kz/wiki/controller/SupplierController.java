package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateSupplierPaymentRequest;
import com.kz.wiki.dto.request.CreateSupplierRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.dto.response.SupplierResponse;
import com.kz.wiki.entity.Supplier;
import com.kz.wiki.entity.SupplierPayment;
import com.kz.wiki.service.SupplierService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Supplier>> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setDefaultMarkup(request.getDefaultMarkup());
        supplier.setNote(request.getNote());
        
        Supplier created = supplierService.create(supplier, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers(
            @RequestParam(required = false) String search) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        List<SupplierResponse> suppliers;
        if (search != null && !search.trim().isEmpty()) {
            suppliers = supplierService.search(search.trim(), tenantId);
        } else {
            suppliers = supplierService.findAll(tenantId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplier(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return supplierService.findById(id, tenantId)
                .map(supplier -> ResponseEntity.ok(ApiResponse.success(supplier)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Supplier not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Supplier>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody CreateSupplierRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setDefaultMarkup(request.getDefaultMarkup());
        supplier.setNote(request.getNote());
        
        Supplier updated = supplierService.update(id, supplier, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        supplierService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Supplier deleted successfully", null));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<SupplierPayment>> addPayment(
            @PathVariable Long id,
            @Valid @RequestBody CreateSupplierPaymentRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        SupplierPayment payment = new SupplierPayment();
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(request.getDate() != null ? request.getDate() : java.time.LocalDate.now());
        
        SupplierPayment created = supplierService.addPayment(id, payment, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment added successfully", created));
    }
}



