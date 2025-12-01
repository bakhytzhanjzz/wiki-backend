package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Product;
import com.kz.wiki.entity.WriteOff;
import com.kz.wiki.entity.WriteOffItem;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.StoreRepository;
import com.kz.wiki.repository.WriteOffItemRepository;
import com.kz.wiki.repository.WriteOffRepository;
import com.kz.wiki.service.StockService;
import com.kz.wiki.service.WriteOffService;
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
public class WriteOffServiceImpl implements WriteOffService {

    private final WriteOffRepository writeOffRepository;
    private final WriteOffItemRepository writeOffItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StockService stockService;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_WRITEOFF", entityType = "WRITEOFF")
    public WriteOff create(WriteOff writeOff, String tenantId, Long userId) {
        if (!storeRepository.existsByIdAndTenantId(writeOff.getStoreId(), tenantId)) {
            throw new ResourceNotFoundException("Store", "id", writeOff.getStoreId());
        }

        writeOff.setTenantId(tenantId);
        writeOff.setCreatedBy(userId);

        WriteOff saved = writeOffRepository.save(writeOff);

        for (WriteOffItem item : writeOff.getItems()) {
            Product product = productRepository.findByIdAndTenantId(item.getProduct().getId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProduct().getId()));

            // Check stock availability
            if (product.getStockQty() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            item.setWriteOff(saved);
            item.setProduct(product);
            item.setTenantId(tenantId);
            writeOffItemRepository.save(item);

            // Record write-off via StockService
            stockService.recordWriteOff(product.getId(), item.getQuantity(),
                    writeOff.getReason() != null ? writeOff.getReason() : "Write-off: " + saved.getName(),
                    tenantId, userId);
        }

        log.info("Write-off created: {} (ID: {}) for tenant: {}", saved.getName(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_WRITEOFF", entityType = "WRITEOFF")
    public WriteOff update(Long id, WriteOff writeOff, String tenantId) {
        WriteOff existing = writeOffRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("WriteOff", "id", id));

        existing.setName(writeOff.getName());
        existing.setStoreId(writeOff.getStoreId());
        existing.setType(writeOff.getType());
        existing.setReason(writeOff.getReason());

        WriteOff updated = writeOffRepository.save(existing);
        log.info("Write-off updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WriteOff> findById(Long id, String tenantId) {
        return writeOffRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WriteOff> findAll(String tenantId) {
        return writeOffRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WriteOff> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return writeOffRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WriteOff> findByStoreId(Long storeId, String tenantId) {
        return writeOffRepository.findByTenantIdAndStoreId(tenantId, storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WriteOff> findByType(String type, String tenantId) {
        return writeOffRepository.findByTenantIdAndType(tenantId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WriteOff> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId) {
        return writeOffRepository.findByTenantIdAndDateRange(tenantId, startDate, endDate);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_WRITEOFF", entityType = "WRITEOFF")
    public void delete(Long id, String tenantId) {
        WriteOff writeOff = writeOffRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("WriteOff", "id", id));

        writeOffRepository.delete(writeOff);
        log.info("Write-off deleted: {} (ID: {}) for tenant: {}", writeOff.getName(), writeOff.getId(), tenantId);
    }
}

