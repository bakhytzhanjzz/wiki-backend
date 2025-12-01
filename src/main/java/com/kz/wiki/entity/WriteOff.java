package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "write_offs")
@Getter
@Setter
public class WriteOff extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private String type; // damaged, expired, lost, other

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @OneToMany(mappedBy = "writeOff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WriteOffItem> items = new ArrayList<>();
}

