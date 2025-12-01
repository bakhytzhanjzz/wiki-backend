package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateProductRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Category;
import com.kz.wiki.entity.Product;
import com.kz.wiki.service.ProductService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setStockQty(request.getStockQty());
        
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            product.setCategory(category);
        }
        
        Product created = productService.create(product, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        List<Product> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productService.search(search.trim(), tenantId);
        } else if (categoryId != null) {
            products = productService.findByCategory(categoryId, tenantId);
        } else {
            products = productService.findAll(tenantId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return productService.findById(id, tenantId)
                .map(product -> ResponseEntity.ok(ApiResponse.success(product)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Product not found")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            product.setCategory(category);
        }
        
        Product updated = productService.update(id, product, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        productService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    @GetMapping("/sku/{sku}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkSkuExists(@PathVariable String sku) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        boolean exists = productService.existsBySku(sku, tenantId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}


