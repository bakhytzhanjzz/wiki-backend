package com.kz.wiki.service;

import com.kz.wiki.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product create(Product product, String tenantId);
    Product update(Long id, Product product, String tenantId);
    Optional<Product> findById(Long id, String tenantId);
    List<Product> findAll(String tenantId);
    List<Product> findByCategory(Long categoryId, String tenantId);
    List<Product> search(String searchTerm, String tenantId);
    void delete(Long id, String tenantId);
    boolean existsBySku(String sku, String tenantId);
}

