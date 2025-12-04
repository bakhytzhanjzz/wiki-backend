package com.kz.wiki.service;

import com.kz.wiki.entity.Repricing;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RepricingService {
    Repricing create(Repricing repricing, String tenantId, Long userId);
    Repricing update(Long id, Repricing repricing, String tenantId);
    Optional<Repricing> findById(Long id, String tenantId);
    List<Repricing> findAll(String tenantId);
    List<Repricing> search(String searchTerm, String tenantId);
    List<Repricing> findByStoreId(Long storeId, String tenantId);
    List<Repricing> findByType(String type, String tenantId);
    List<Repricing> findByDate(LocalDate date, String tenantId);
    void delete(Long id, String tenantId);
}




