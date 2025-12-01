package com.kz.wiki.repository;

import com.kz.wiki.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTenantId(String tenantId);
    List<AuditLog> findByTenantIdAndUserId(String tenantId, Long userId);
}




