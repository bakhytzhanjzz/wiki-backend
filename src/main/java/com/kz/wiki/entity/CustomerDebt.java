package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_debts")
@Getter
@Setter
public class CustomerDebt extends BaseTenantEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "sale_id")
    private Long saleId; // Optional, can be null for manual debts

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_amount", precision = 19, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "remaining_amount", precision = 19, scale = 2)
    private BigDecimal remainingAmount;

    @Column(nullable = false)
    private String status = "unpaid"; // unpaid, paid, partial, overdue

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate = LocalDateTime.now();

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "payment_type")
    private String paymentType; // cash, card, etc.

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_overdue")
    private Boolean isOverdue = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}



