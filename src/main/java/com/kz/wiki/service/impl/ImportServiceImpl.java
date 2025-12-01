package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Import;
import com.kz.wiki.entity.ImportItem;
import com.kz.wiki.entity.Product;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.ImportItemRepository;
import com.kz.wiki.repository.ImportRepository;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.StoreRepository;
import com.kz.wiki.service.ImportService;
import com.kz.wiki.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final ImportRepository importRepository;
    private final ImportItemRepository importItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StockService stockService;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_IMPORT", entityType = "IMPORT")
    public Import create(Import importEntity, String tenantId, Long userId) {
        // Validate store
        if (!storeRepository.existsByIdAndTenantId(importEntity.getStoreId(), tenantId)) {
            throw new ResourceNotFoundException("Store", "id", importEntity.getStoreId());
        }

        importEntity.setTenantId(tenantId);
        importEntity.setCreatedBy(userId);
        importEntity.setStatus("pending");

        // Save import first
        Import saved = importRepository.save(importEntity);

        // Save items and update stock
        for (ImportItem item : importEntity.getItems()) {
            // Validate product
            Product product = productRepository.findByIdAndTenantId(item.getProduct().getId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProduct().getId()));

            item.setImportEntity(saved);
            item.setProduct(product);
            item.setTenantId(tenantId);
            importItemRepository.save(item);

            // Update stock via StockService
            stockService.recordReceipt(
                    product.getId(),
                    item.getQuantity(),
                    "Import: " + saved.getName(),
                    tenantId,
                    userId
            );
        }

        // Mark as completed
        saved.setStatus("completed");
        saved = importRepository.save(saved);

        log.info("Import created: {} (ID: {}) for tenant: {}", saved.getName(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_IMPORT", entityType = "IMPORT")
    public Import update(Long id, Import importEntity, String tenantId) {
        Import existing = importRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Import", "id", id));

        if ("completed".equals(existing.getStatus())) {
            throw new BadRequestException("Cannot update completed import");
        }

        existing.setName(importEntity.getName());
        existing.setStoreId(importEntity.getStoreId());
        existing.setStatus(importEntity.getStatus());

        Import updated = importRepository.save(existing);
        log.info("Import updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Import> findById(Long id, String tenantId) {
        return importRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Import> findAll(String tenantId) {
        return importRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Import> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return importRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Import> findByStoreId(Long storeId, String tenantId) {
        return importRepository.findByTenantIdAndStoreId(tenantId, storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Import> findByStatus(String status, String tenantId) {
        return importRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Import> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId) {
        return importRepository.findByTenantIdAndDateRange(tenantId, startDate, endDate);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_IMPORT", entityType = "IMPORT")
    public void delete(Long id, String tenantId) {
        Import importEntity = importRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Import", "id", id));

        if ("completed".equals(importEntity.getStatus())) {
            throw new BadRequestException("Cannot delete completed import");
        }

        importRepository.delete(importEntity);
        log.info("Import deleted: {} (ID: {}) for tenant: {}", importEntity.getName(), importEntity.getId(), tenantId);
    }
}

