package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateSaleRequest;
import com.kz.wiki.dto.request.CreateReturnRequest;
import com.kz.wiki.dto.request.CreateExchangeRequest;
import com.kz.wiki.dto.request.CancelSaleRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Sale>> createSale(@Valid @RequestBody CreateSaleRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        Sale sale = new Sale();
        sale.setStoreId(request.getStoreId());
        sale.setCustomerId(request.getCustomerId());
        sale.setSellerId(request.getSellerId());
        sale.setSubtotal(request.getSubtotal());
        sale.setDiscount(request.getDiscount());
        sale.setDiscountType(request.getDiscountType());
        sale.setDiscountCode(request.getDiscountCode());
        sale.setTotalAmount(request.getTotalAmount());
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setLoyaltyPointsUsed(request.getLoyaltyPointsUsed());
        sale.setDebtPaymentAmount(request.getDebtPaymentAmount());
        sale.setNote(request.getNote());
        sale.setStatus(request.getIsDraft() != null && request.getIsDraft() ? "draft" : 
                      (request.getIsDeferred() != null && request.getIsDeferred() ? "deferred" : "completed"));
        
        for (CreateSaleRequest.SaleItemRequest itemRequest : request.getItems()) {
            SaleItem item = new SaleItem();
            Product product = new Product();
            product.setId(itemRequest.getProductId());
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());
            item.setWholesalePrice(itemRequest.getWholesalePrice());
            item.setDiscount(itemRequest.getDiscount());
            item.setDiscountType(itemRequest.getDiscountType());
            sale.getItems().add(item);
        }
        
        Sale created = saleService.createSale(sale, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sale created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Sale>>> getAllSales(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        SaleService.SaleFilters filters = new SaleService.SaleFilters() {
            @Override
            public String getSearch() { return search; }
            @Override
            public Long getStoreId() { return storeId; }
            @Override
            public String getPaymentMethod() { return paymentMethod; }
            @Override
            public Long getSellerId() { return sellerId; }
            @Override
            public Long getCustomerId() { return customerId; }
            @Override
            public BigDecimal getMinAmount() { return minAmount; }
            @Override
            public BigDecimal getMaxAmount() { return maxAmount; }
            @Override
            public LocalDate getStartDate() { return startDate; }
            @Override
            public LocalDate getEndDate() { return endDate; }
            @Override
            public String getStatus() { return status; }
            @Override
            public String getType() { return type; }
            @Override
            public Integer getPage() { return page; }
            @Override
            public Integer getLimit() { return limit; }
        };
        
        List<Sale> sales = saleService.findWithFilters(filters, tenantId);
        return ResponseEntity.ok(ApiResponse.success(sales));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Sale>> getSale(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return saleService.findById(id, tenantId)
                .map(sale -> ResponseEntity.ok(ApiResponse.success(sale)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Sale not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Sale>> updateSale(
            @PathVariable Long id,
            @Valid @RequestBody CreateSaleRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Sale sale = new Sale();
        sale.setStoreId(request.getStoreId());
        sale.setCustomerId(request.getCustomerId());
        sale.setSellerId(request.getSellerId());
        sale.setSubtotal(request.getSubtotal());
        sale.setDiscount(request.getDiscount());
        sale.setDiscountType(request.getDiscountType());
        sale.setDiscountCode(request.getDiscountCode());
        sale.setTotalAmount(request.getTotalAmount());
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setNote(request.getNote());
        
        Sale updated = saleService.updateSale(id, sale, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Sale updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSale(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        // Note: Delete only allowed for drafts/deferred
        return ResponseEntity.ok(ApiResponse.success("Sale deleted successfully", null));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Sale>> cancelSale(
            @PathVariable Long id,
            @Valid @RequestBody CancelSaleRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        Sale cancelled = saleService.cancelSale(id, request.getReason(), tenantId, userId);
        return ResponseEntity.ok(ApiResponse.success("Sale cancelled successfully", cancelled));
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Sale>> createReturn(
            @PathVariable Long id,
            @Valid @RequestBody CreateReturnRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        SaleService.CreateReturnRequest returnRequest = new SaleService.CreateReturnRequest() {
            @Override
            public List<ReturnItemRequest> getItems() {
                return request.getItems().stream()
                        .map(item -> new ReturnItemRequest() {
                            @Override
                            public Long getSaleItemId() { return item.getSaleItemId(); }
                            @Override
                            public Integer getQuantity() { return item.getQuantity(); }
                            @Override
                            public String getReason() { return item.getReason(); }
                        })
                        .collect(Collectors.toList());
            }
            @Override
            public String getRefundMethod() { return request.getRefundMethod(); }
            @Override
            public String getNote() { return request.getNote(); }
        };
        
        Sale returnSale = saleService.createReturn(id, returnRequest, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Return processed successfully", returnSale));
    }

    @GetMapping("/returns")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Sale>>> getAllReturns(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<Sale> returns = saleService.findReturns(tenantId);
        return ResponseEntity.ok(ApiResponse.success(returns));
    }

    @GetMapping("/returns/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Sale>> getReturn(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return saleService.findById(id, tenantId)
                .map(sale -> ResponseEntity.ok(ApiResponse.success(sale)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Return not found")));
    }

    @PostMapping("/{id}/exchange")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Sale>> createExchange(
            @PathVariable Long id,
            @Valid @RequestBody CreateExchangeRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        SaleService.CreateExchangeRequest exchangeRequest = new SaleService.CreateExchangeRequest() {
            @Override
            public List<ReturnItemRequest> getReturnItems() {
                return request.getReturnItems().stream()
                        .map(item -> new ReturnItemRequest() {
                            @Override
                            public Long getSaleItemId() { return item.getSaleItemId(); }
                            @Override
                            public Integer getQuantity() { return item.getQuantity(); }
                            @Override
                            public String getReason() { return item.getReason(); }
                        })
                        .collect(Collectors.toList());
            }
            @Override
            public List<NewItemRequest> getNewItems() {
                return request.getNewItems().stream()
                        .map(item -> new NewItemRequest() {
                            @Override
                            public Long getProductId() { return item.getProductId(); }
                            @Override
                            public Integer getQuantity() { return item.getQuantity(); }
                            @Override
                            public BigDecimal getPrice() { return item.getPrice(); }
                        })
                        .collect(Collectors.toList());
            }
            @Override
            public BigDecimal getDifferenceAmount() { return request.getDifferenceAmount(); }
            @Override
            public String getPaymentMethod() { return request.getPaymentMethod(); }
            @Override
            public String getNote() { return request.getNote(); }
        };
        
        Sale exchange = saleService.createExchange(id, exchangeRequest, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exchange processed successfully", exchange));
    }

    @GetMapping("/exchanges")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Sale>>> getAllExchanges(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<Sale> exchanges = saleService.findExchanges(tenantId);
        return ResponseEntity.ok(ApiResponse.success(exchanges));
    }

    @GetMapping("/exchanges/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Sale>> getExchange(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return saleService.findById(id, tenantId)
                .map(sale -> ResponseEntity.ok(ApiResponse.success(sale)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Exchange not found")));
    }

    @GetMapping("/drafts")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<List<Sale>>> getAllDrafts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<Sale> drafts = saleService.findDrafts(tenantId);
        return ResponseEntity.ok(ApiResponse.success(drafts));
    }

    @PostMapping("/drafts")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Sale>> saveDraft(@Valid @RequestBody CreateSaleRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        Sale sale = buildSaleFromRequest(request);
        sale.setStatus("draft");
        
        Sale saved = saleService.saveDraft(sale, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Draft saved successfully", saved));
    }

    @PostMapping("/drafts/{id}/complete")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Sale>> completeDraft(
            @PathVariable Long id,
            @RequestBody(required = false) CreateSaleRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        Sale sale = request != null ? buildSaleFromRequest(request) : null;
        Sale completed = saleService.completeDraft(id, sale, tenantId, userId);
        return ResponseEntity.ok(ApiResponse.success("Draft completed successfully", completed));
    }

    @GetMapping("/deferred")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<List<Sale>>> getAllDeferred(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<Sale> deferred = saleService.findDeferred(tenantId);
        return ResponseEntity.ok(ApiResponse.success(deferred));
    }

    @PostMapping("/deferred")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Sale>> saveDeferred(@Valid @RequestBody CreateSaleRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        Sale sale = buildSaleFromRequest(request);
        sale.setStatus("deferred");
        
        Sale saved = saleService.saveDeferred(sale, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deferred sale saved successfully", saved));
    }

    @PostMapping("/deferred/{id}/complete")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Sale>> completeDeferred(
            @PathVariable Long id,
            @RequestBody(required = false) CreateSaleRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();
        
        Sale sale = request != null ? buildSaleFromRequest(request) : null;
        Sale completed = saleService.completeDeferred(id, sale, tenantId, userId);
        return ResponseEntity.ok(ApiResponse.success("Deferred sale completed successfully", completed));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<SaleService.SaleStatistics>> getStatistics(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String groupBy) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        SaleService.SaleStatisticsFilters filters = new SaleService.SaleStatisticsFilters() {
            @Override
            public Long getStoreId() { return storeId; }
            @Override
            public Long getSellerId() { return sellerId; }
            @Override
            public LocalDate getStartDate() { return startDate; }
            @Override
            public LocalDate getEndDate() { return endDate; }
            @Override
            public String getGroupBy() { return groupBy; }
        };
        
        SaleService.SaleStatistics statistics = saleService.getStatistics(filters, tenantId);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/statistics/by-date")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<SaleService.SaleStatisticsByDate>> getStatisticsByDate(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "day") String groupBy) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        SaleService.SaleStatisticsByDate statistics = saleService.getStatisticsByDate(startDate, endDate, groupBy, tenantId);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/next-transaction-number")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<TransactionNumberResponse>> getNextTransactionNumber(
            @RequestParam(required = false) Long storeId) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        String transactionNumber = saleService.getNextTransactionNumber(storeId, tenantId);
        String receiptNumber = transactionNumber.replace("TXN", "RCP");
        return ResponseEntity.ok(ApiResponse.success(new TransactionNumberResponse(transactionNumber, receiptNumber)));
    }

    private Sale buildSaleFromRequest(CreateSaleRequest request) {
        Sale sale = new Sale();
        sale.setStoreId(request.getStoreId());
        sale.setCustomerId(request.getCustomerId());
        sale.setSellerId(request.getSellerId());
        sale.setSubtotal(request.getSubtotal());
        sale.setDiscount(request.getDiscount());
        sale.setDiscountType(request.getDiscountType());
        sale.setDiscountCode(request.getDiscountCode());
        sale.setTotalAmount(request.getTotalAmount());
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setLoyaltyPointsUsed(request.getLoyaltyPointsUsed());
        sale.setDebtPaymentAmount(request.getDebtPaymentAmount());
        sale.setNote(request.getNote());
        
        for (CreateSaleRequest.SaleItemRequest itemRequest : request.getItems()) {
            SaleItem item = new SaleItem();
            Product product = new Product();
            product.setId(itemRequest.getProductId());
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());
            item.setWholesalePrice(itemRequest.getWholesalePrice());
            item.setDiscount(itemRequest.getDiscount());
            item.setDiscountType(itemRequest.getDiscountType());
            sale.getItems().add(item);
        }
        
        return sale;
    }

    private static class TransactionNumberResponse {
        private final String transactionNumber;
        private final String receiptNumber;

        public TransactionNumberResponse(String transactionNumber, String receiptNumber) {
            this.transactionNumber = transactionNumber;
            this.receiptNumber = receiptNumber;
        }

        public String getTransactionNumber() { return transactionNumber; }
        public String getReceiptNumber() { return receiptNumber; }
    }
}
