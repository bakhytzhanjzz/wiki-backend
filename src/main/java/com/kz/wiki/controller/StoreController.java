package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateStoreRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Store;
import com.kz.wiki.service.StoreService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Store>> createStore(@Valid @RequestBody CreateStoreRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Store store = new Store();
        store.setName(request.getName());
        store.setAddress(request.getAddress());
        
        Store created = storeService.create(store, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Store created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Store>>> getAllStores() {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<Store> stores = storeService.findAll(tenantId);
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Store>> getStore(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return storeService.findById(id, tenantId)
                .map(store -> ResponseEntity.ok(ApiResponse.success(store)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Store not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Store>> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody CreateStoreRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Store store = new Store();
        store.setName(request.getName());
        store.setAddress(request.getAddress());
        
        Store updated = storeService.update(id, store, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Store updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        storeService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Store deleted successfully", null));
    }
}

