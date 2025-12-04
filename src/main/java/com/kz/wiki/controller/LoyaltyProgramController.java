package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateLoyaltyLevelRequest;
import com.kz.wiki.dto.request.CreateLoyaltyProgramRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.dto.response.LoyaltyProgramResponse;
import com.kz.wiki.service.LoyaltyProgramService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/loyalty-program")
@RequiredArgsConstructor
public class LoyaltyProgramController {

    private final LoyaltyProgramService programService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProgram() {
        String tenantId = SecurityUtil.getCurrentTenantId();
        LoyaltyProgramResponse program = programService.get(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", program);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProgram(@Valid @RequestBody CreateLoyaltyProgramRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        LoyaltyProgramResponse program = programService.update(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", program);
        return ResponseEntity.ok(ApiResponse.success("Program updated successfully", response));
    }

    @PostMapping("/levels")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createLevel(@Valid @RequestBody CreateLoyaltyLevelRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        LoyaltyProgramResponse program = programService.createLevel(request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", program);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Level created successfully", response));
    }

    @PutMapping("/levels/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateLevel(
            @PathVariable Long id,
            @Valid @RequestBody CreateLoyaltyLevelRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        LoyaltyProgramResponse program = programService.updateLevel(id, request, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", program);
        return ResponseEntity.ok(ApiResponse.success("Level updated successfully", response));
    }

    @DeleteMapping("/levels/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteLevel(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        LoyaltyProgramResponse program = programService.deleteLevel(id, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", program);
        return ResponseEntity.ok(ApiResponse.success("Level deleted successfully", response));
    }
}

