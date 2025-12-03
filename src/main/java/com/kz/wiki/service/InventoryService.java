package com.kz.wiki.service;

import com.kz.wiki.entity.Inventory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryService {
    Inventory create(Inventory inventory, String tenantId, Long userId);
    Inventory update(Long id, Inventory inventory, String tenantId);
    Inventory complete(Long id, String tenantId, Long userId);
    Optional<Inventory> findById(Long id, String tenantId);
    List<Inventory> findAll(String tenantId);
    List<Inventory> search(String searchTerm, String tenantId);
    List<Inventory> findByStoreId(Long storeId, String tenantId);
    List<Inventory> findByType(String type, String tenantId);
    List<Inventory> findByStatus(String status, String tenantId);
    List<Inventory> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId);
    void delete(Long id, String tenantId);
}


