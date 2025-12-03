package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateInventoryRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Inventory;
import com.kz.wiki.service.InventoryService;
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
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Inventory>> createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();

        Inventory inventory = new Inventory();
        inventory.setName(request.getName());
        inventory.setStoreId(request.getStoreId());
        inventory.setType(request.getType());

        Inventory created = inventoryService.create(inventory, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inventory created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Inventory>>> getAllInventories(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        List<Inventory> inventories;
        if (search != null && !search.trim().isEmpty()) {
            inventories = inventoryService.search(search.trim(), tenantId);
        } else if (storeId != null) {
            inventories = inventoryService.findByStoreId(storeId, tenantId);
        } else if (type != null && !type.trim().isEmpty()) {
            inventories = inventoryService.findByType(type.trim(), tenantId);
        } else if (status != null && !status.trim().isEmpty()) {
            inventories = inventoryService.findByStatus(status.trim(), tenantId);
        } else if (startDate != null && endDate != null) {
            inventories = inventoryService.findByDateRange(startDate, endDate, tenantId);
        } else {
            inventories = inventoryService.findAll(tenantId);
        }

        return ResponseEntity.ok(ApiResponse.success(inventories));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Inventory>> getInventory(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return inventoryService.findById(id, tenantId)
                .map(inventory -> ResponseEntity.ok(ApiResponse.success(inventory)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Inventory not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Inventory>> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody CreateInventoryRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        Inventory inventory = new Inventory();
        inventory.setName(request.getName());
        inventory.setStoreId(request.getStoreId());
        inventory.setType(request.getType());

        Inventory updated = inventoryService.update(id, inventory, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Inventory updated successfully", updated));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Inventory>> completeInventory(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();

        Inventory completed = inventoryService.complete(id, tenantId, userId);
        return ResponseEntity.ok(ApiResponse.success("Inventory completed successfully", completed));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        inventoryService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Inventory deleted successfully", null));
    }
}


