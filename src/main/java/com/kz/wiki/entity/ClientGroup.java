package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_groups")
@Getter
@Setter
public class ClientGroup extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "discount_percent")
    private Integer discountPercent = 0;

    @Column(name = "discount_application")
    private String discountApplication; // retail_only, wholesale_only, both

    @Column(nullable = false)
    private String status = "open"; // open, closed

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}


