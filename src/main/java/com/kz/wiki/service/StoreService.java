package com.kz.wiki.service;

import com.kz.wiki.entity.Store;
import java.util.List;
import java.util.Optional;

public interface StoreService {
    Store create(Store store, String tenantId);
    Store update(Long id, Store store, String tenantId);
    Optional<Store> findById(Long id, String tenantId);
    List<Store> findAll(String tenantId);
    void delete(Long id, String tenantId);
}




