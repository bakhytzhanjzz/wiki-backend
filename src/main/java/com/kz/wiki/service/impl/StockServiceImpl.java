package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Product;
import com.kz.wiki.entity.StockTransaction;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.StockTransactionRepository;
import com.kz.wiki.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockTransactionRepository stockTransactionRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "STOCK_RECEIPT", entityType = "STOCK")
    public StockTransaction recordReceipt(Long productId, Integer quantity, String reason, String tenantId, Long userId) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        Product product = productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Create stock transaction
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setQuantityChange(quantity);
        transaction.setReason(reason != null ? reason : "Stock receipt");
        transaction.setTenantId(tenantId);
        transaction.setCreatedBy(userId);
        transaction.setCreatedAt(LocalDateTime.now());

        // Update product stock
        int newStock = product.getStockQty() + quantity;
        product.setStockQty(newStock);

        productRepository.save(product);
        StockTransaction saved = stockTransactionRepository.save(transaction);

        log.info("Stock receipt recorded: {} units for product {} (ID: {}) for tenant: {}", 
                quantity, product.getName(), productId, tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "STOCK_WRITEOFF", entityType = "STOCK")
    public StockTransaction recordWriteOff(Long productId, Integer quantity, String reason, String tenantId, Long userId) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        Product product = productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Check if sufficient stock available
        if (product.getStockQty() < quantity) {
            throw new BadRequestException(
                    String.format("Insufficient stock. Available: %d, Requested: %d", 
                            product.getStockQty(), quantity));
        }

        // Create stock transaction (negative quantity)
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setQuantityChange(-quantity);
        transaction.setReason(reason != null ? reason : "Stock write-off");
        transaction.setTenantId(tenantId);
        transaction.setCreatedBy(userId);
        transaction.setCreatedAt(LocalDateTime.now());

        // Update product stock
        int newStock = product.getStockQty() - quantity;
        product.setStockQty(newStock);

        productRepository.save(product);
        StockTransaction saved = stockTransactionRepository.save(transaction);

        log.info("Stock write-off recorded: {} units for product {} (ID: {}) for tenant: {}", 
                quantity, product.getName(), productId, tenantId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransaction> getProductHistory(Long productId, String tenantId) {
        // Verify product exists
        productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return stockTransactionRepository.findByTenantIdAndProductId(tenantId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCurrentStock(Long productId, String tenantId) {
        Product product = productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return product.getStockQty();
    }
}


