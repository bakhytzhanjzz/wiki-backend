package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.dto.response.SupplierResponse;
import com.kz.wiki.entity.Product;
import com.kz.wiki.entity.Supplier;
import com.kz.wiki.entity.SupplierPayment;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.SupplierPaymentRepository;
import com.kz.wiki.repository.SupplierRepository;
import com.kz.wiki.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierPaymentRepository paymentRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_SUPPLIER", entityType = "SUPPLIER")
    public Supplier create(Supplier supplier, String tenantId) {
        supplier.setTenantId(tenantId);
        Supplier saved = supplierRepository.save(supplier);
        log.info("Supplier created: {} for tenant: {}", saved.getName(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_SUPPLIER", entityType = "SUPPLIER")
    public Supplier update(Long id, Supplier supplier, String tenantId) {
        Supplier existing = supplierRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        existing.setName(supplier.getName());
        existing.setPhone(supplier.getPhone());
        existing.setDefaultMarkup(supplier.getDefaultMarkup());
        existing.setNote(supplier.getNote());
        
        Supplier updated = supplierRepository.save(existing);
        log.info("Supplier updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierResponse> findById(Long id, String tenantId) {
        return supplierRepository.findByIdAndTenantId(id, tenantId)
                .map(this::toSupplierResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> findAll(String tenantId) {
        return supplierRepository.findByTenantId(tenantId).stream()
                .map(this::toSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return supplierRepository.searchByTenantId(tenantId, searchTerm.trim()).stream()
                .map(this::toSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_SUPPLIER", entityType = "SUPPLIER")
    public void delete(Long id, String tenantId) {
        Supplier supplier = supplierRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        supplierRepository.delete(supplier);
        log.info("Supplier deleted: {} (ID: {}) for tenant: {}", supplier.getName(), supplier.getId(), tenantId);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "ADD_SUPPLIER_PAYMENT", entityType = "SUPPLIER_PAYMENT")
    public SupplierPayment addPayment(Long supplierId, SupplierPayment payment, String tenantId, Long userId) {
        if (!supplierRepository.existsByIdAndTenantId(supplierId, tenantId)) {
            throw new ResourceNotFoundException("Supplier", "id", supplierId);
        }
        
        payment.setSupplierId(supplierId);
        payment.setTenantId(tenantId);
        payment.setCreatedBy(userId);
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        
        SupplierPayment saved = paymentRepository.save(payment);
        log.info("Payment added for supplier {}: {} for tenant: {}", supplierId, payment.getAmount(), tenantId);
        return saved;
    }

    private SupplierResponse toSupplierResponse(Supplier supplier) {
        String tenantId = supplier.getTenantId();
        Long supplierId = supplier.getId();
        
        // Calculate payments amount
        BigDecimal paymentsAmount = paymentRepository.getTotalPaymentsBySupplierId(supplierId, tenantId);
        if (paymentsAmount == null) {
            paymentsAmount = BigDecimal.ZERO;
        }
        
        // Count products for this supplier
        List<Product> products = productRepository.findByTenantId(tenantId).stream()
                .filter(p -> p.getSupplierId() != null && p.getSupplierId().equals(supplierId))
                .collect(Collectors.toList());
        int productsCount = products.size();
        
        // Calculate orders amount (simplified - would need Order/Import entities for accurate calculation)
        BigDecimal ordersAmount = BigDecimal.ZERO; // TODO: Calculate from imports/orders
        
        // Calculate debt (orders - payments)
        BigDecimal debtAmount = ordersAmount.subtract(paymentsAmount);
        if (debtAmount.compareTo(BigDecimal.ZERO) < 0) {
            debtAmount = BigDecimal.ZERO;
        }
        
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .defaultMarkup(supplier.getDefaultMarkup())
                .note(supplier.getNote())
                .debtAmount(debtAmount)
                .ordersAmount(ordersAmount)
                .paymentsAmount(paymentsAmount)
                .productsCount(productsCount)
                .build();
    }
}


