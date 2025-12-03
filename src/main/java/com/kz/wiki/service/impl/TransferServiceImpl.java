package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Product;
import com.kz.wiki.entity.Transfer;
import com.kz.wiki.entity.TransferItem;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.StoreRepository;
import com.kz.wiki.repository.TransferItemRepository;
import com.kz.wiki.repository.TransferRepository;
import com.kz.wiki.service.StockService;
import com.kz.wiki.service.TransferService;
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
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferItemRepository transferItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StockService stockService;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_TRANSFER", entityType = "TRANSFER")
    public Transfer create(Transfer transfer, String tenantId, Long userId) {
        if (!storeRepository.existsByIdAndTenantId(transfer.getFromStoreId(), tenantId) ||
            !storeRepository.existsByIdAndTenantId(transfer.getToStoreId(), tenantId)) {
            throw new ResourceNotFoundException("Store", "id", transfer.getFromStoreId());
        }

        transfer.setTenantId(tenantId);
        transfer.setCreatedBy(userId);
        transfer.setStatus("pending");
        transfer.setSentAt(LocalDateTime.now());

        Transfer saved = transferRepository.save(transfer);

        for (TransferItem item : transfer.getItems()) {
            Product product = productRepository.findByIdAndTenantId(item.getProduct().getId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProduct().getId()));

            // Check stock availability
            if (product.getStockQty() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            item.setTransfer(saved);
            item.setProduct(product);
            item.setTenantId(tenantId);
            transferItemRepository.save(item);

            // Deduct from source store
            stockService.recordWriteOff(product.getId(), item.getQuantity(),
                    "Transfer: " + saved.getName(), tenantId, userId);
        }

        saved.setStatus("in_transit");
        saved = transferRepository.save(saved);

        log.info("Transfer created: {} (ID: {}) for tenant: {}", saved.getName(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_TRANSFER", entityType = "TRANSFER")
    public Transfer update(Long id, Transfer transfer, String tenantId) {
        Transfer existing = transferRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer", "id", id));

        if ("received".equals(existing.getStatus())) {
            throw new BadRequestException("Cannot update received transfer");
        }

        existing.setName(transfer.getName());
        existing.setFromStoreId(transfer.getFromStoreId());
        existing.setToStoreId(transfer.getToStoreId());
        existing.setStatus(transfer.getStatus());

        Transfer updated = transferRepository.save(existing);
        log.info("Transfer updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "RECEIVE_TRANSFER", entityType = "TRANSFER")
    public Transfer receive(Long id, String tenantId, Long userId) {
        Transfer transfer = transferRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer", "id", id));

        if ("received".equals(transfer.getStatus())) {
            throw new BadRequestException("Transfer is already received");
        }

        for (TransferItem item : transfer.getItems()) {
            // Add to destination store
            stockService.recordReceipt(item.getProduct().getId(), item.getQuantity(),
                    "Transfer received: " + transfer.getName(), tenantId, userId);
        }

        transfer.setStatus("received");
        transfer.setReceivedAt(LocalDateTime.now());
        transfer.setReceivedBy(userId);
        Transfer received = transferRepository.save(transfer);

        log.info("Transfer received: {} (ID: {}) for tenant: {}", received.getName(), received.getId(), tenantId);
        return received;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transfer> findById(Long id, String tenantId) {
        return transferRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transfer> findAll(String tenantId) {
        return transferRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transfer> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return transferRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transfer> findByFromStoreId(Long fromStoreId, String tenantId) {
        return transferRepository.findByTenantIdAndFromStoreId(tenantId, fromStoreId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transfer> findByToStoreId(Long toStoreId, String tenantId) {
        return transferRepository.findByTenantIdAndToStoreId(tenantId, toStoreId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transfer> findByStatus(String status, String tenantId) {
        return transferRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transfer> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId) {
        return transferRepository.findByTenantIdAndSentDateRange(tenantId, startDate, endDate);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_TRANSFER", entityType = "TRANSFER")
    public void delete(Long id, String tenantId) {
        Transfer transfer = transferRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer", "id", id));

        if ("received".equals(transfer.getStatus())) {
            throw new BadRequestException("Cannot delete received transfer");
        }

        transferRepository.delete(transfer);
        log.info("Transfer deleted: {} (ID: {}) for tenant: {}", transfer.getName(), transfer.getId(), tenantId);
    }
}


