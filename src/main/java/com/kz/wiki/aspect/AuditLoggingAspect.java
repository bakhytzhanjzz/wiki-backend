package com.kz.wiki.aspect;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.service.AuditLogService;
import com.kz.wiki.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLoggingAspect {

    private final AuditLogService auditLogService;

    @Around("@annotation(com.kz.wiki.annotation.AuditLoggable)")
    public Object logAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuditLoggable annotation = method.getAnnotation(AuditLoggable.class);

        String action = annotation.action();
        String entityType = annotation.entityType();
        Object[] args = joinPoint.getArgs();

        try {
            String tenantId = SecurityUtil.getCurrentTenantId();
            Long userId = SecurityUtil.getCurrentUserId();

            Object result = joinPoint.proceed();

            // Extract entity ID from result if possible
            Long entityId = extractEntityId(result);
            String details = buildDetails(entityType, entityId, args);

            if (entityId != null && !entityType.isEmpty()) {
                switch (entityType.toUpperCase()) {
                    case "PRODUCT":
                        auditLogService.logProductAction(action, entityId, details, tenantId, userId);
                        break;
                    case "SALE":
                        auditLogService.logSaleAction(action, entityId, details, tenantId, userId);
                        break;
                    case "STOCK":
                        auditLogService.logStockAction(action, entityId, details, tenantId, userId);
                        break;
                    case "USER":
                        auditLogService.logUserAction(action, entityId, details, tenantId, userId);
                        break;
                    default:
                        auditLogService.logAction(action, details, tenantId, userId);
                }
            } else {
                auditLogService.logAction(action, details, tenantId, userId);
            }

            return result;
        } catch (Exception e) {
            log.error("Error in audit logging", e);
            return joinPoint.proceed();
        }
    }

    private Long extractEntityId(Object result) {
        if (result == null) return null;
        try {
            if (result instanceof org.springframework.http.ResponseEntity) {
                Object body = ((org.springframework.http.ResponseEntity<?>) result).getBody();
                if (body != null) {
                    return extractIdFromObject(body);
                }
            }
            return extractIdFromObject(result);
        } catch (Exception e) {
            return null;
        }
    }

    private Long extractIdFromObject(Object obj) {
        try {
            Method getIdMethod = obj.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(obj);
            if (id instanceof Long) {
                return (Long) id;
            }
        } catch (Exception e) {
            // Try to get from nested object (ApiResponse)
            try {
                Method getDataMethod = obj.getClass().getMethod("getData");
                Object data = getDataMethod.invoke(obj);
                return extractIdFromObject(data);
            } catch (Exception ex) {
                // Ignore
            }
        }
        return null;
    }

    private String buildDetails(String entityType, Long entityId, Object[] args) {
        StringBuilder details = new StringBuilder();
        if (entityId != null) {
            details.append("Entity ID: ").append(entityId);
        }
        if (args != null && args.length > 0) {
            details.append(" | Args: ").append(args.length).append(" parameters");
        }
        return details.toString();
    }
}





