package com.kz.wiki.service.impl;

import com.kz.wiki.entity.Product;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.CategoryRepository;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Product create(Product product, String tenantId) {
        // Validate SKU uniqueness
        if (productRepository.existsBySkuAndTenantId(product.getSku(), tenantId)) {
            throw new BadRequestException("Product with SKU '" + product.getSku() + "' already exists");
        }

        // Validate category if provided
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            if (!categoryRepository.existsByIdAndTenantId(product.getCategory().getId(), tenantId)) {
                throw new BadRequestException("Category not found");
            }
        }

        product.setTenantId(tenantId);
        product.setStockQty(product.getStockQty() != null ? product.getStockQty() : 0);
        
        Product saved = productRepository.save(product);
        log.info("Product created: {} (SKU: {}) for tenant: {}", saved.getName(), saved.getSku(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    public Product update(Long id, Product product, String tenantId) {
        Product existing = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Check SKU uniqueness if changed
        if (!existing.getSku().equals(product.getSku()) && 
            productRepository.existsBySkuAndTenantId(product.getSku(), tenantId)) {
            throw new BadRequestException("Product with SKU '" + product.getSku() + "' already exists");
        }

        // Validate category if provided
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            if (!categoryRepository.existsByIdAndTenantId(product.getCategory().getId(), tenantId)) {
                throw new BadRequestException("Category not found");
            }
        }

        existing.setName(product.getName());
        existing.setSku(product.getSku());
        existing.setPrice(product.getPrice());
        existing.setCategory(product.getCategory());
        // Note: stockQty should be updated via StockService, not directly here

        Product updated = productRepository.save(existing);
        log.info("Product updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Product> findById(Long id, String tenantId) {
        return productRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll(String tenantId) {
        return productRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Long categoryId, String tenantId) {
        if (!categoryRepository.existsByIdAndTenantId(categoryId, tenantId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        return productRepository.findByTenantIdAndCategoryId(tenantId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return productRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional
    public void delete(Long id, String tenantId) {
        Product product = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        productRepository.delete(product);
        log.info("Product deleted: {} (ID: {}) for tenant: {}", product.getName(), product.getId(), tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku, String tenantId) {
        return productRepository.existsBySkuAndTenantId(sku, tenantId);
    }
}


