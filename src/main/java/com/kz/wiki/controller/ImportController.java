package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateImportRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Import;
import com.kz.wiki.entity.ImportItem;
import com.kz.wiki.entity.Product;
import com.kz.wiki.service.ImportService;
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
@RequestMapping("/api/imports")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Import>> createImport(@Valid @RequestBody CreateImportRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();

        Import importEntity = new Import();
        importEntity.setName(request.getName());
        importEntity.setStoreId(request.getStoreId());

        for (CreateImportRequest.ImportItemRequest itemRequest : request.getItems()) {
            ImportItem item = new ImportItem();
            Product product = new Product();
            product.setId(itemRequest.getProductId());
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());
            importEntity.getItems().add(item);
        }

        Import created = importService.create(importEntity, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Import created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Import>>> getAllImports(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        List<Import> imports;
        if (search != null && !search.trim().isEmpty()) {
            imports = importService.search(search.trim(), tenantId);
        } else if (storeId != null) {
            imports = importService.findByStoreId(storeId, tenantId);
        } else if (status != null && !status.trim().isEmpty()) {
            imports = importService.findByStatus(status.trim(), tenantId);
        } else if (startDate != null && endDate != null) {
            imports = importService.findByDateRange(startDate, endDate, tenantId);
        } else {
            imports = importService.findAll(tenantId);
        }

        return ResponseEntity.ok(ApiResponse.success(imports));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Import>> getImport(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return importService.findById(id, tenantId)
                .map(importEntity -> ResponseEntity.ok(ApiResponse.success(importEntity)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Import not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Import>> updateImport(
            @PathVariable Long id,
            @Valid @RequestBody CreateImportRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        Import importEntity = new Import();
        importEntity.setName(request.getName());
        importEntity.setStoreId(request.getStoreId());

        Import updated = importService.update(id, importEntity, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Import updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteImport(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        importService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Import deleted successfully", null));
    }
}




