package com.kz.wiki.controller;

import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Product;
import com.kz.wiki.entity.Store;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.StoreRepository;
import com.kz.wiki.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/test-data")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class TestDataController {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<String>> initializeTestData() {
        String tenantId = SecurityUtil.getCurrentTenantId();

        // Create a test store if it doesn't exist
        Store store = storeRepository.findByTenantId(tenantId).stream()
                .findFirst()
                .orElseGet(() -> {
                    Store newStore = new Store();
                    newStore.setTenantId(tenantId);
                    newStore.setName("Main Store");
                    newStore.setAddress("123 Main Street");
                    return storeRepository.save(newStore);
                });

        // Create test products if they don't exist
        if (productRepository.findByTenantId(tenantId).isEmpty()) {
            // Product 1
            Product product1 = new Product();
            product1.setTenantId(tenantId);
            product1.setName("Test Product 1");
            product1.setSku("TEST-001");
            product1.setPrice(new BigDecimal("100.00"));
            product1.setStockQty(100);
            product1.setBarcode("1234567890123");
            product1.setUnit("pcs");
            productRepository.save(product1);

            // Product 2
            Product product2 = new Product();
            product2.setTenantId(tenantId);
            product2.setName("Test Product 2");
            product2.setSku("TEST-002");
            product2.setPrice(new BigDecimal("50.00"));
            product2.setStockQty(50);
            product2.setBarcode("1234567890124");
            product2.setUnit("pcs");
            productRepository.save(product2);

            // Product 3
            Product product3 = new Product();
            product3.setTenantId(tenantId);
            product3.setName("Test Product 3");
            product3.setSku("TEST-003");
            product3.setPrice(new BigDecimal("200.00"));
            product3.setStockQty(25);
            product3.setBarcode("1234567890125");
            product3.setUnit("pcs");
            productRepository.save(product3);
        }

        return ResponseEntity.ok(ApiResponse.success(
                "Test data initialized successfully. Created store (ID: " + store.getId() + ") and test products."
        ));
    }
}


