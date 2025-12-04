package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateClientTagRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.dto.response.ClientTagResponse;
import com.kz.wiki.dto.response.PaginationResponse;
import com.kz.wiki.service.ClientTagService;
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
@RequestMapping("/api/client-tags")
@RequiredArgsConstructor
public class ClientTagController {

    private final ClientTagService tagService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllTags(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Pageable pageable = PageRequest.of(page - 1, Math.min(limit, 100));
        Page<ClientTagResponse> tags = tagService.findAll(tenantId, pageable, search, type, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", tags.getContent());
        response.put("pagination", PaginationResponse.builder()
                .page(page)
                .limit(limit)
                .total(tags.getTotalElements())
                .totalPages(tags.getTotalPages())
                .build());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTag(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        ClientTagResponse tag = tagService.findById(id, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", tag);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Map<String, Object> stats = tagService.getStatistics(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", stats);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTag(@Valid @RequestBody CreateClientTagRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        ClientTagResponse tag = tagService.create(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", tag);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tag created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody CreateClientTagRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        ClientTagResponse tag = tagService.update(id, request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", tag);
        return ResponseEntity.ok(ApiResponse.success("Tag updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        tagService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Tag deleted successfully", null));
    }
}

