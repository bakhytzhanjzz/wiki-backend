package com.kz.wiki.controller;

import com.kz.wiki.dto.request.*;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.dto.response.ClientResponse;
import com.kz.wiki.dto.response.PaginationResponse;
import com.kz.wiki.service.ClientService;
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
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllClients(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> groupIds,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdayFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdayTo,
            @RequestParam(required = false) BigDecimal purchaseAmountFrom,
            @RequestParam(required = false) BigDecimal purchaseAmountTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastPurchaseFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastPurchaseTo,
            @RequestParam(required = false) Integer noPurchaseDays,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registrationFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registrationTo,
            @RequestParam(required = false) List<Long> registrationStoreIds,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Pageable pageable = PageRequest.of(page - 1, Math.min(limit, 100));
        Page<ClientResponse> clients = clientService.findAll(tenantId, pageable, search, groupIds, tagIds,
                birthdayFrom, birthdayTo, purchaseAmountFrom, purchaseAmountTo,
                lastPurchaseFrom, lastPurchaseTo, noPurchaseDays,
                registrationFrom, registrationTo, registrationStoreIds, gender);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", clients.getContent());
        response.put("pagination", PaginationResponse.builder()
                .page(page)
                .limit(limit)
                .total(clients.getTotalElements())
                .totalPages(clients.getTotalPages())
                .build());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<ClientResponse>> getClient(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        ClientResponse client = clientService.findById(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(client));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Object> stats = clientService.getStatistics(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", stats);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(@Valid @RequestBody CreateClientRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        ClientResponse client = clientService.create(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Client created successfully", client));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody CreateClientRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        ClientResponse client = clientService.update(id, request, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Client updated successfully", client));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        clientService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Client deleted successfully", null));
    }

    @PostMapping("/bulk-update")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkUpdate(@Valid @RequestBody BulkUpdateClientsRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Integer> result = clientService.bulkUpdate(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bulk-assign-groups")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkAssignGroups(@Valid @RequestBody BulkAssignGroupsRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Integer> result = clientService.bulkAssignGroups(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bulk-remove-groups")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkRemoveGroups(@Valid @RequestBody BulkAssignGroupsRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Integer> result = clientService.bulkRemoveGroups(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bulk-assign-tags")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkAssignTags(@Valid @RequestBody BulkAssignTagsRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Integer> result = clientService.bulkAssignTags(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bulk-remove-tags")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkRemoveTags(@Valid @RequestBody BulkAssignTagsRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Integer> result = clientService.bulkRemoveTags(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bulk-delete")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkDelete(@Valid @RequestBody BulkDeleteClientsRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Integer> result = clientService.bulkDelete(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> importClients(@RequestParam("file") MultipartFile file) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Object> result = clientService.importClients(file, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/debts")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchForDebts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "true") Boolean hasDebt) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<ClientResponse> clients = clientService.searchForDebts(search, tenantId, hasDebt);
        Map<String, Object> response = new HashMap<>();
        response.put("data", clients);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

