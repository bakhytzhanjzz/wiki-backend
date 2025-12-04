package com.kz.wiki.service;

import com.kz.wiki.entity.WriteOff;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WriteOffService {
    WriteOff create(WriteOff writeOff, String tenantId, Long userId);
    WriteOff update(Long id, WriteOff writeOff, String tenantId);
    Optional<WriteOff> findById(Long id, String tenantId);
    List<WriteOff> findAll(String tenantId);
    List<WriteOff> search(String searchTerm, String tenantId);
    List<WriteOff> findByStoreId(Long storeId, String tenantId);
    List<WriteOff> findByType(String type, String tenantId);
    List<WriteOff> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId);
    void delete(Long id, String tenantId);
}



