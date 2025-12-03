package com.kz.wiki.service;

import com.kz.wiki.entity.Import;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ImportService {
    Import create(Import importEntity, String tenantId, Long userId);
    Import update(Long id, Import importEntity, String tenantId);
    Optional<Import> findById(Long id, String tenantId);
    List<Import> findAll(String tenantId);
    List<Import> search(String searchTerm, String tenantId);
    List<Import> findByStoreId(Long storeId, String tenantId);
    List<Import> findByStatus(String status, String tenantId);
    List<Import> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId);
    void delete(Long id, String tenantId);
}


