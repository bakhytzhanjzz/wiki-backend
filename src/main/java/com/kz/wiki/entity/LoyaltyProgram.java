package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loyalty_programs")
@Getter
@Setter
public class LoyaltyProgram extends BaseTenantEntity {

    @Column(nullable = false)
    private String type; // discount, points

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "loyaltyProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoyaltyLevel> levels = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}

