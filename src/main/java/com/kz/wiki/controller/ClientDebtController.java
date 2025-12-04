package com.kz.wiki.controller;

import com.kz.wiki.dto.request.*;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.dto.response.ClientDebtResponse;
import com.kz.wiki.dto.response.DebtRepaymentResponse;
import com.kz.wiki.dto.response.PaginationResponse;
import com.kz.wiki.service.ClientDebtService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client-debts")
@RequiredArgsConstructor
public class ClientDebtController {

    private final ClientDebtService debtService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllDebts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) BigDecimal repaymentAmountFrom,
            @RequestParam(required = false) BigDecimal repaymentAmountTo,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDateTo,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Pageable pageable = PageRequest.of(page - 1, Math.min(limit, 100));
        Page<ClientDebtResponse> debts = debtService.findAll(tenantId, pageable, search, status, storeId, paymentType,
                repaymentAmountFrom, repaymentAmountTo, clientId, userId, issueDateFrom, issueDateTo);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", debts.getContent());
        response.put("pagination", PaginationResponse.builder()
                .page(page)
                .limit(limit)
                .total(debts.getTotalElements())
                .totalPages(debts.getTotalPages())
                .build());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/repayments")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRepayments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) BigDecimal repaymentAmountFrom,
            @RequestParam(required = false) BigDecimal repaymentAmountTo,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate repaymentDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate repaymentDateTo,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Pageable pageable = PageRequest.of(page - 1, Math.min(limit, 100));
        Page<DebtRepaymentResponse> repayments = debtService.findRepayments(tenantId, pageable, search, storeId, paymentType,
                repaymentAmountFrom, repaymentAmountTo, clientId, userId, repaymentDateFrom, repaymentDateTo);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", repayments.getContent());
        response.put("pagination", PaginationResponse.builder()
                .page(page)
                .limit(limit)
                .total(repayments.getTotalElements())
                .totalPages(repayments.getTotalPages())
                .build());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDateTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate repaymentDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate repaymentDateTo) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Object> stats = debtService.getStatistics(tenantId, issueDateFrom, issueDateTo, repaymentDateFrom, repaymentDateTo);
        Map<String, Object> response = new HashMap<>();
        response.put("data", stats);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createDebt(@Valid @RequestBody CreateClientDebtRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        ClientDebtResponse debt = debtService.create(request, tenantId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", debt);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Debt created successfully", response));
    }

    @PostMapping("/{id}/repay")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> repayDebt(
            @PathVariable Long id,
            @Valid @RequestBody RepayDebtRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        DebtRepaymentResponse repayment = debtService.repay(id, request, tenantId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", repayment);
        return ResponseEntity.ok(ApiResponse.success("Debt repaid successfully", response));
    }

    @PostMapping("/bulk-repay")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkRepay(@Valid @RequestBody BulkRepayDebtsRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        Map<String, Object> result = debtService.bulkRepay(request, tenantId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/send-sms")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendSms(@Valid @RequestBody SendSmsToDebtorsRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Object> result = debtService.sendSms(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

