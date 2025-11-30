package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies", uniqueConstraints = {
        @UniqueConstraint(columnNames = "tenant_id")
})
@Getter
@Setter
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private String tenantId; // e.g., "cafe-almaty-789"

    private String name;
    private String ownerEmail;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}