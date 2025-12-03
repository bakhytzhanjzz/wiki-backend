package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateTransferRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Transfer;
import com.kz.wiki.entity.TransferItem;
import com.kz.wiki.entity.Product;
import com.kz.wiki.service.TransferService;
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
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Transfer>> createTransfer(@Valid @RequestBody CreateTransferRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();

        Transfer transfer = new Transfer();
        transfer.setName(request.getName());
        transfer.setFromStoreId(request.getFromStoreId());
        transfer.setToStoreId(request.getToStoreId());

        for (CreateTransferRequest.TransferItemRequest itemRequest : request.getItems()) {
            TransferItem item = new TransferItem();
            Product product = new Product();
            product.setId(itemRequest.getProductId());
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            transfer.getItems().add(item);
        }

        Transfer created = transferService.create(transfer, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Transfer>>> getAllTransfers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long fromStoreId,
            @RequestParam(required = false) Long toStoreId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        List<Transfer> transfers;
        if (search != null && !search.trim().isEmpty()) {
            transfers = transferService.search(search.trim(), tenantId);
        } else if (fromStoreId != null) {
            transfers = transferService.findByFromStoreId(fromStoreId, tenantId);
        } else if (toStoreId != null) {
            transfers = transferService.findByToStoreId(toStoreId, tenantId);
        } else if (status != null && !status.trim().isEmpty()) {
            transfers = transferService.findByStatus(status.trim(), tenantId);
        } else if (startDate != null && endDate != null) {
            transfers = transferService.findByDateRange(startDate, endDate, tenantId);
        } else {
            transfers = transferService.findAll(tenantId);
        }

        return ResponseEntity.ok(ApiResponse.success(transfers));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Transfer>> getTransfer(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return transferService.findById(id, tenantId)
                .map(transfer -> ResponseEntity.ok(ApiResponse.success(transfer)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Transfer not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Transfer>> updateTransfer(
            @PathVariable Long id,
            @Valid @RequestBody CreateTransferRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        Transfer transfer = new Transfer();
        transfer.setName(request.getName());
        transfer.setFromStoreId(request.getFromStoreId());
        transfer.setToStoreId(request.getToStoreId());

        Transfer updated = transferService.update(id, transfer, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Transfer updated successfully", updated));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Transfer>> receiveTransfer(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();

        Transfer received = transferService.receive(id, tenantId, userId);
        return ResponseEntity.ok(ApiResponse.success("Transfer received successfully", received));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTransfer(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        transferService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Transfer deleted successfully", null));
    }
}


