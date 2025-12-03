package com.kz.wiki.service;

import com.kz.wiki.dto.response.SupplierResponse;
import com.kz.wiki.entity.Supplier;
import com.kz.wiki.entity.SupplierPayment;
import java.util.List;
import java.util.Optional;

public interface SupplierService {
    Supplier create(Supplier supplier, String tenantId);
    Supplier update(Long id, Supplier supplier, String tenantId);
    Optional<SupplierResponse> findById(Long id, String tenantId);
    List<SupplierResponse> findAll(String tenantId);
    List<SupplierResponse> search(String searchTerm, String tenantId);
    void delete(Long id, String tenantId);
    SupplierPayment addPayment(Long supplierId, SupplierPayment payment, String tenantId, Long userId);
}


