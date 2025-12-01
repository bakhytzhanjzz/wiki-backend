package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Order;
import com.kz.wiki.entity.OrderItem;
import com.kz.wiki.entity.Product;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.OrderItemRepository;
import com.kz.wiki.repository.OrderRepository;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.StoreRepository;
import com.kz.wiki.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_ORDER", entityType = "ORDER")
    public Order create(Order order, String tenantId, Long userId) {
        // Validate store
        if (!storeRepository.existsByIdAndTenantId(order.getStoreId(), tenantId)) {
            throw new ResourceNotFoundException("Store", "id", order.getStoreId());
        }

        order.setTenantId(tenantId);
        order.setCreatedBy(userId);
        order.setStatus("pending");
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findByIdAndTenantId(item.getProduct().getId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProduct().getId()));

            item.setProduct(product);
            item.setPrice(product.getPrice());
            item.setTenantId(tenantId);
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        Order saved = orderRepository.save(order);

        // Save items
        for (OrderItem item : saved.getItems()) {
            item.setOrder(saved);
            orderItemRepository.save(item);
        }

        log.info("Order created: {} (ID: {}) for tenant: {}", saved.getOrderNumber(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_ORDER", entityType = "ORDER")
    public Order update(Long id, Order order, String tenantId) {
        Order existing = orderRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if ("completed".equals(existing.getStatus())) {
            throw new BadRequestException("Cannot update completed order");
        }

        existing.setStoreId(order.getStoreId());
        existing.setCustomerId(order.getCustomerId());
        existing.setStatus(order.getStatus());
        existing.setPaymentMethod(order.getPaymentMethod());

        Order updated = orderRepository.save(existing);
        log.info("Order updated: {} (ID: {}) for tenant: {}", updated.getOrderNumber(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id, String tenantId) {
        return orderRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll(String tenantId) {
        return orderRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return orderRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStoreId(Long storeId, String tenantId) {
        return orderRepository.findByTenantIdAndStoreId(tenantId, storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatus(String status, String tenantId) {
        return orderRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId) {
        return orderRepository.findByTenantIdAndDateRange(tenantId, startDate, endDate);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_ORDER", entityType = "ORDER")
    public void delete(Long id, String tenantId) {
        Order order = orderRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if ("completed".equals(order.getStatus())) {
            throw new BadRequestException("Cannot delete completed order");
        }

        orderRepository.delete(order);
        log.info("Order deleted: {} (ID: {}) for tenant: {}", order.getOrderNumber(), order.getId(), tenantId);
    }
}

