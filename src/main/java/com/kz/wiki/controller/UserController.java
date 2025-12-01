package com.kz.wiki.controller;

import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.User;
import com.kz.wiki.entity.UserRole;
import com.kz.wiki.repository.UserRepository;
import com.kz.wiki.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<User> users = userRepository.findByTenantId(tenantId);
        // Remove passwords from response
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return userRepository.findByIdAndTenantId(id, tenantId)
                .map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(ApiResponse.success(user));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found")));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<User>> updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateRoleRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        User user = userRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new com.kz.wiki.exception.ResourceNotFoundException("User", "id", id));
        
        user.setRole(request.getRole());
        User updated = userRepository.save(user);
        updated.setPassword(null);
        
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", updated));
    }

    @lombok.Data
    public static class UpdateRoleRequest {
        private UserRole role;
    }
}

