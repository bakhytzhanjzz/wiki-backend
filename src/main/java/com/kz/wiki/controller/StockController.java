package com.kz.wiki.controller;

import com.kz.wiki.dto.request.StockTransactionRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.StockTransaction;
import com.kz.wiki.service.StockService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/receipt")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<StockTransaction>> recordReceipt(
            @Valid @RequestBody StockTransactionRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        StockTransaction transaction = stockService.recordReceipt(
                request.getProductId(),
                request.getQuantity(),
                request.getReason(),
                tenantId,
                userId
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock receipt recorded successfully", transaction));
    }

    @PostMapping("/write-off")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<StockTransaction>> recordWriteOff(
            @Valid @RequestBody StockTransactionRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        StockTransaction transaction = stockService.recordWriteOff(
                request.getProductId(),
                request.getQuantity(),
                request.getReason(),
                tenantId,
                userId
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock write-off recorded successfully", transaction));
    }

    @GetMapping("/product/{productId}/history")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<StockTransaction>>> getProductHistory(@PathVariable Long productId) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<StockTransaction> history = stockService.getProductHistory(productId, tenantId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/product/{productId}/current")
    public ResponseEntity<ApiResponse<Integer>> getCurrentStock(@PathVariable Long productId) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Integer stock = stockService.getCurrentStock(productId, tenantId);
        return ResponseEntity.ok(ApiResponse.success(stock));
    }
}


