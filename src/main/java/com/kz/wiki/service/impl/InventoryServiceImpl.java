package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Inventory;
import com.kz.wiki.entity.InventoryItem;
import com.kz.wiki.entity.Product;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.InventoryItemRepository;
import com.kz.wiki.repository.InventoryRepository;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.StoreRepository;
import com.kz.wiki.service.InventoryService;
import com.kz.wiki.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StockService stockService;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_INVENTORY", entityType = "INVENTORY")
    public Inventory create(Inventory inventory, String tenantId, Long userId) {
        if (!storeRepository.existsByIdAndTenantId(inventory.getStoreId(), tenantId)) {
            throw new ResourceNotFoundException("Store", "id", inventory.getStoreId());
        }

        inventory.setTenantId(tenantId);
        inventory.setCreatedBy(userId);
        inventory.setStatus("in_progress");

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Inventory created: {} (ID: {}) for tenant: {}", saved.getName(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_INVENTORY", entityType = "INVENTORY")
    public Inventory update(Long id, Inventory inventory, String tenantId) {
        Inventory existing = inventoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));

        if ("completed".equals(existing.getStatus())) {
            throw new BadRequestException("Cannot update completed inventory");
        }

        existing.setName(inventory.getName());
        existing.setStoreId(inventory.getStoreId());
        existing.setType(inventory.getType());

        Inventory updated = inventoryRepository.save(existing);
        log.info("Inventory updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "COMPLETE_INVENTORY", entityType = "INVENTORY")
    public Inventory complete(Long id, String tenantId, Long userId) {
        Inventory inventory = inventoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));

        if ("completed".equals(inventory.getStatus())) {
            throw new BadRequestException("Inventory is already completed");
        }

        // Process inventory items and adjust stock
        for (InventoryItem item : inventory.getItems()) {
            Product product = productRepository.findByIdAndTenantId(item.getProduct().getId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProduct().getId()));
            
            item.setProduct(product);
            item.setTenantId(tenantId);
            inventoryItemRepository.save(item);
            
            int difference = item.getActualQty() - item.getExpectedQty();
            item.setDifference(difference);

            if (difference != 0) {
                // Adjust stock based on difference
                if (difference > 0) {
                    stockService.recordReceipt(product.getId(), difference, 
                            "Inventory adjustment: " + inventory.getName(), tenantId, userId);
                } else {
                    stockService.recordWriteOff(product.getId(), Math.abs(difference), 
                            "Inventory adjustment: " + inventory.getName(), tenantId, userId);
                }
            }
        }

        inventory.setStatus("completed");
        inventory.setCompletedAt(LocalDateTime.now());
        Inventory completed = inventoryRepository.save(inventory);

        log.info("Inventory completed: {} (ID: {}) for tenant: {}", completed.getName(), completed.getId(), tenantId);
        return completed;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventory> findById(Long id, String tenantId) {
        return inventoryRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findAll(String tenantId) {
        return inventoryRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return inventoryRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByStoreId(Long storeId, String tenantId) {
        return inventoryRepository.findByTenantIdAndStoreId(tenantId, storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByType(String type, String tenantId) {
        return inventoryRepository.findByTenantIdAndType(tenantId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByStatus(String status, String tenantId) {
        return inventoryRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId) {
        return inventoryRepository.findByTenantIdAndDateRange(tenantId, startDate, endDate);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_INVENTORY", entityType = "INVENTORY")
    public void delete(Long id, String tenantId) {
        Inventory inventory = inventoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));

        if ("completed".equals(inventory.getStatus())) {
            throw new BadRequestException("Cannot delete completed inventory");
        }

        inventoryRepository.delete(inventory);
        log.info("Inventory deleted: {} (ID: {}) for tenant: {}", inventory.getName(), inventory.getId(), tenantId);
    }
}

