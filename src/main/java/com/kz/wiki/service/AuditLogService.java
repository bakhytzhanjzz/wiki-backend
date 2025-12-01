package com.kz.wiki.service;

public interface AuditLogService {
    void logAction(String action, String details, String tenantId, Long userId);
    void logProductAction(String action, Long productId, String details, String tenantId, Long userId);
    void logSaleAction(String action, Long saleId, String details, String tenantId, Long userId);
    void logStockAction(String action, Long productId, String details, String tenantId, Long userId);
    void logUserAction(String action, Long targetUserId, String details, String tenantId, Long userId);
}

