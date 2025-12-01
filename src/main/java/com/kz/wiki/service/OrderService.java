package com.kz.wiki.service;

import com.kz.wiki.entity.Order;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order create(Order order, String tenantId, Long userId);
    Order update(Long id, Order order, String tenantId);
    Optional<Order> findById(Long id, String tenantId);
    List<Order> findAll(String tenantId);
    List<Order> search(String searchTerm, String tenantId);
    List<Order> findByStoreId(Long storeId, String tenantId);
    List<Order> findByStatus(String status, String tenantId);
    List<Order> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId);
    void delete(Long id, String tenantId);
}

