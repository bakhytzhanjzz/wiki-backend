package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateClientGroupRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.dto.response.ClientGroupResponse;
import com.kz.wiki.dto.response.PaginationResponse;
import com.kz.wiki.service.ClientGroupService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client-groups")
@RequiredArgsConstructor
public class ClientGroupController {

    private final ClientGroupService groupService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllGroups(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Pageable pageable = PageRequest.of(page - 1, Math.min(limit, 100));
        Page<ClientGroupResponse> groups = groupService.findAll(tenantId, pageable, search, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", groups.getContent());
        response.put("pagination", PaginationResponse.builder()
                .page(page)
                .limit(limit)
                .total(groups.getTotalElements())
                .totalPages(groups.getTotalPages())
                .build());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGroup(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        ClientGroupResponse group = groupService.findById(id, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", group);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Object> stats = groupService.getStatistics(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", stats);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createGroup(@Valid @RequestBody CreateClientGroupRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        ClientGroupResponse group = groupService.create(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", group);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Group created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody CreateClientGroupRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        ClientGroupResponse group = groupService.update(id, request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", group);
        return ResponseEntity.ok(ApiResponse.success("Group updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        groupService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully", null));
    }
}

