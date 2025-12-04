package com.kz.wiki.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kz.wiki.entity.AuditLog;
import com.kz.wiki.repository.AuditLogRepository;
import com.kz.wiki.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void logAction(String action, String details, String tenantId, Long userId) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setDetails(details);
            auditLog.setTenantId(tenantId);
            auditLog.setUserId(userId);
            auditLog.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to log audit action: {}", action, e);
        }
    }

    @Override
    @Transactional
    public void logProductAction(String action, Long productId, String details, String tenantId, Long userId) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("productId", productId);
        logData.put("details", details);
        logAction(action, toJson(logData), tenantId, userId);
    }

    @Override
    @Transactional
    public void logSaleAction(String action, Long saleId, String details, String tenantId, Long userId) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("saleId", saleId);
        logData.put("details", details);
        logAction(action, toJson(logData), tenantId, userId);
    }

    @Override
    @Transactional
    public void logStockAction(String action, Long productId, String details, String tenantId, Long userId) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("productId", productId);
        logData.put("details", details);
        logAction(action, toJson(logData), tenantId, userId);
    }

    @Override
    @Transactional
    public void logUserAction(String action, Long targetUserId, String details, String tenantId, Long userId) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("targetUserId", targetUserId);
        logData.put("details", details);
        logAction(action, toJson(logData), tenantId, userId);
    }

    private String toJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            return data.toString();
        }
    }
}






