package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Product;
import com.kz.wiki.entity.Repricing;
import com.kz.wiki.entity.RepricingItem;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.RepricingItemRepository;
import com.kz.wiki.repository.RepricingRepository;
import com.kz.wiki.repository.StoreRepository;
import com.kz.wiki.service.RepricingService;
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
public class RepricingServiceImpl implements RepricingService {

    private final RepricingRepository repricingRepository;
    private final RepricingItemRepository repricingItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_REPRICING", entityType = "REPRICING")
    public Repricing create(Repricing repricing, String tenantId, Long userId) {
        if (!storeRepository.existsByIdAndTenantId(repricing.getStoreId(), tenantId)) {
            throw new ResourceNotFoundException("Store", "id", repricing.getStoreId());
        }

        repricing.setTenantId(tenantId);
        repricing.setCreatedBy(userId);

        Repricing saved = repricingRepository.save(repricing);

        for (RepricingItem item : repricing.getItems()) {
            Product product = productRepository.findByIdAndTenantId(item.getProduct().getId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProduct().getId()));

            item.setRepricing(saved);
            item.setProduct(product);
            item.setTenantId(tenantId);
            repricingItemRepository.save(item);

            // Update product price
            product.setPrice(item.getNewPrice());
            productRepository.save(product);
        }

        log.info("Repricing created: {} (ID: {}) for tenant: {}", saved.getName(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_REPRICING", entityType = "REPRICING")
    public Repricing update(Long id, Repricing repricing, String tenantId) {
        Repricing existing = repricingRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Repricing", "id", id));

        existing.setName(repricing.getName());
        existing.setStoreId(repricing.getStoreId());
        existing.setType(repricing.getType());

        Repricing updated = repricingRepository.save(existing);
        log.info("Repricing updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Repricing> findById(Long id, String tenantId) {
        return repricingRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Repricing> findAll(String tenantId) {
        return repricingRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Repricing> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return repricingRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Repricing> findByStoreId(Long storeId, String tenantId) {
        return repricingRepository.findByTenantIdAndStoreId(tenantId, storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Repricing> findByType(String type, String tenantId) {
        return repricingRepository.findByTenantIdAndType(tenantId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Repricing> findByDate(LocalDate date, String tenantId) {
        return repricingRepository.findByTenantIdAndDate(tenantId, date);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_REPRICING", entityType = "REPRICING")
    public void delete(Long id, String tenantId) {
        Repricing repricing = repricingRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Repricing", "id", id));

        repricingRepository.delete(repricing);
        log.info("Repricing deleted: {} (ID: {}) for tenant: {}", repricing.getName(), repricing.getId(), tenantId);
    }
}

