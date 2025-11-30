package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog extends BaseTenantEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String action; // e.g., "CREATE_SALE", "UPDATE_PRODUCT"

    @Column(columnDefinition = "TEXT")
    private String details; // JSON или просто строка

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}