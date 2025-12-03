package com.kz.wiki.service;

import com.kz.wiki.entity.Transfer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransferService {
    Transfer create(Transfer transfer, String tenantId, Long userId);
    Transfer update(Long id, Transfer transfer, String tenantId);
    Transfer receive(Long id, String tenantId, Long userId);
    Optional<Transfer> findById(Long id, String tenantId);
    List<Transfer> findAll(String tenantId);
    List<Transfer> search(String searchTerm, String tenantId);
    List<Transfer> findByFromStoreId(Long fromStoreId, String tenantId);
    List<Transfer> findByToStoreId(Long toStoreId, String tenantId);
    List<Transfer> findByStatus(String status, String tenantId);
    List<Transfer> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId);
    void delete(Long id, String tenantId);
}


