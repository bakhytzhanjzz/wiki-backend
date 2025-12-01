package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "imports")
@Getter
@Setter
public class Import extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "status", nullable = false)
    private String status = "pending"; // pending, completed, cancelled

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @OneToMany(mappedBy = "importEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImportItem> items = new ArrayList<>();
}

