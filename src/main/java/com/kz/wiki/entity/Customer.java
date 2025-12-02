package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "phone"}),
        @UniqueConstraint(columnNames = {"tenant_id", "email"})
})
@Getter
@Setter
public class Customer extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    private String phone;

    private String email;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Column(name = "total_purchases", precision = 19, scale = 2)
    private BigDecimal totalPurchases = BigDecimal.ZERO;

    @Column(name = "total_transactions")
    private Integer totalTransactions = 0;

    @Column(name = "last_purchase_date")
    private LocalDateTime lastPurchaseDate;

    @Column(name = "debt_amount", precision = 19, scale = 2)
    private BigDecimal debtAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

