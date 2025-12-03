package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateOrderRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Order;
import com.kz.wiki.entity.OrderItem;
import com.kz.wiki.entity.Product;
import com.kz.wiki.service.OrderService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Order>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();

        Order order = new Order();
        order.setStoreId(request.getStoreId());
        order.setCustomerId(request.getCustomerId());
        order.setPaymentMethod(request.getPaymentMethod());

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            Product product = new Product();
            product.setId(itemRequest.getProductId());
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            order.getItems().add(item);
        }

        Order created = orderService.create(order, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        List<Order> orders;
        if (search != null && !search.trim().isEmpty()) {
            orders = orderService.search(search.trim(), tenantId);
        } else if (status != null && !status.trim().isEmpty()) {
            orders = orderService.findByStatus(status.trim(), tenantId);
        } else if (storeId != null) {
            orders = orderService.findByStoreId(storeId, tenantId);
        } else if (startDate != null && endDate != null) {
            orders = orderService.findByDateRange(startDate, endDate, tenantId);
        } else {
            orders = orderService.findAll(tenantId);
        }

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return orderService.findById(id, tenantId)
                .map(order -> ResponseEntity.ok(ApiResponse.success(order)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Order not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Order>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody CreateOrderRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        Order order = new Order();
        order.setStoreId(request.getStoreId());
        order.setCustomerId(request.getCustomerId());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("pending");

        Order updated = orderService.update(id, order, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Order updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        orderService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
    }
}


