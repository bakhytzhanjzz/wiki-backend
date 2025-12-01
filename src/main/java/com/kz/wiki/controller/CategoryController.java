package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateCategoryRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Category;
import com.kz.wiki.service.CategoryService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Category category = new Category();
        category.setName(request.getName());
        
        Category created = categoryService.create(category, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<Category> categories = categoryService.findAll(tenantId);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategory(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return categoryService.findById(id, tenantId)
                .map(category -> ResponseEntity.ok(ApiResponse.success(category)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Category not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Category category = new Category();
        category.setName(request.getName());
        
        Category updated = categoryService.update(id, category, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        categoryService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}

