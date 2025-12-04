package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "phone"}),
        @UniqueConstraint(columnNames = {"tenant_id", "email"})
})
@Getter
@Setter
public class Customer extends BaseTenantEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(nullable = false)
    private String name; // Full name constructed from firstName, lastName, middleName

    @Column(nullable = false)
    private String phone;

    @Column(name = "phones", columnDefinition = "TEXT")
    private String phones; // JSON array stored as text

    private String email;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "gender")
    private String gender; // male, female

    @Column(name = "marital_status")
    private String maritalStatus; // single, married, divorced, widowed

    @Column(name = "language")
    private String language; // ru, kk, en

    @Column(name = "registration_store_id")
    private Long registrationStoreId;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Column(name = "loyalty_level")
    private String loyaltyLevel;

    @Column(name = "total_purchases", precision = 19, scale = 2)
    private BigDecimal totalPurchases = BigDecimal.ZERO;

    @Column(name = "last_purchase_date")
    private LocalDateTime lastPurchaseDate;

    @Column(name = "debt_amount", precision = 19, scale = 2)
    private BigDecimal debtAmount = BigDecimal.ZERO;

    // Social networks (stored as JSON)
    @Column(name = "social_networks", columnDefinition = "TEXT")
    private String socialNetworks; // JSON: {telegram, facebook, instagram}

    // Notifications preferences (stored as JSON)
    @Column(name = "notifications", columnDefinition = "TEXT")
    private String notifications; // JSON: {sms, phone, social, email}

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}



