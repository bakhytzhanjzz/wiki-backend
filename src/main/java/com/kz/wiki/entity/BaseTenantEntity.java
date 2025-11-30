package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseTenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    protected String tenantId;

    // Можно добавить audit позже: createdAt, createdBy и т.д.
}
