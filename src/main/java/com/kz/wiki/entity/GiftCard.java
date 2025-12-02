package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_cards", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "code"}),
        @UniqueConstraint(columnNames = {"tenant_id", "number"})
})
@Getter
@Setter
public class GiftCard extends BaseTenantEntity {

    @Column(nullable = false, unique = false)
    private String code;

    @Column(nullable = false, unique = false)
    private String number;

    @Column(nullable = false)
    private String type; // certificate, voucher

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "remaining_amount", precision = 19, scale = 2)
    private BigDecimal remainingAmount;

    @Column(nullable = false)
    private String status = "active"; // active, used, expired, cancelled

    @Column(name = "issued_at")
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "issued_by")
    private Long issuedBy;

    @Column(name = "store_id")
    private Long storeId;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

