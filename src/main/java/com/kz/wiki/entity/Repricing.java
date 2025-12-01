package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repricings")
@Getter
@Setter
public class Repricing extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private String type; // markup, discount, fixed

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @OneToMany(mappedBy = "repricing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepricingItem> items = new ArrayList<>();
}

