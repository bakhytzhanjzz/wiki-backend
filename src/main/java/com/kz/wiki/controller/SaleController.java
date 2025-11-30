package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateSaleRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Product;
import com.kz.wiki.entity.Sale;
import com.kz.wiki.entity.SaleItem;
import com.kz.wiki.service.SaleService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<ApiResponse<Sale>> createSale(@Valid @RequestBody CreateSaleRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        Sale sale = new Sale();
        for (CreateSaleRequest.SaleItemRequest itemRequest : request.getItems()) {
            SaleItem item = new SaleItem();
            Product product = new Product();
            product.setId(itemRequest.getProductId());
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            sale.getItems().add(item);
        }
        
        Sale created = saleService.createSale(sale, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sale created successfully", created));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<Sale>> createReturn(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        Sale returnSale = saleService.createReturn(id, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Return processed successfully", returnSale));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Sale>>> getAllSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        List<Sale> sales;
        if (startDate != null && endDate != null) {
            sales = saleService.findByDateRange(startDate, endDate, tenantId);
        } else {
            sales = saleService.findAll(tenantId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(sales));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Sale>> getSale(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return saleService.findById(id, tenantId)
                .map(sale -> ResponseEntity.ok(ApiResponse.success(sale)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Sale not found")));
    }
}

