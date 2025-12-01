package com.kz.wiki.service;

import com.kz.wiki.entity.Sale;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SaleService {
    Sale createSale(Sale sale, String tenantId, Long userId);
    Sale createReturn(Long saleId, String tenantId, Long userId);
    Optional<Sale> findById(Long id, String tenantId);
    List<Sale> findAll(String tenantId);
    List<Sale> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId);
}




