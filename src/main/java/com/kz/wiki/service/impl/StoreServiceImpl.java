package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Store;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.StoreRepository;
import com.kz.wiki.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_STORE", entityType = "STORE")
    public Store create(Store store, String tenantId) {
        store.setTenantId(tenantId);
        Store saved = storeRepository.save(store);
        log.info("Store created: {} for tenant: {}", saved.getName(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_STORE", entityType = "STORE")
    public Store update(Long id, Store store, String tenantId) {
        Store existing = storeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        
        existing.setName(store.getName());
        existing.setAddress(store.getAddress());
        
        Store updated = storeRepository.save(existing);
        log.info("Store updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Store> findById(Long id, String tenantId) {
        return storeRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Store> findAll(String tenantId) {
        return storeRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_STORE", entityType = "STORE")
    public void delete(Long id, String tenantId) {
        Store store = storeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", id));
        
        storeRepository.delete(store);
        log.info("Store deleted: {} (ID: {}) for tenant: {}", store.getName(), store.getId(), tenantId);
    }
}




