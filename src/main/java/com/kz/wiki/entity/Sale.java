package com.kz.wiki.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter
@Setter
public class Sale extends BaseTenantEntity {

    @Column(name = "transaction_number", unique = true)
    private String transactionNumber;

    @Column(name = "receipt_number", unique = true)
    private String receiptNumber;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "subtotal", precision = 19, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount", precision = 19, scale = 2)
    private BigDecimal discount;

    @Column(name = "discount_type")
    private String discountType; // percentage, fixed

    @Column(name = "discount_code")
    private String discountCode;

    @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "loyalty_points_used")
    private Integer loyaltyPointsUsed = 0;

    @Column(name = "loyalty_points_earned")
    private Integer loyaltyPointsEarned = 0;

    @Column(name = "debt_payment_amount", precision = 19, scale = 2)
    private BigDecimal debtPaymentAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private String status = "completed"; // completed, cancelled, draft, deferred

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleType type = SaleType.SALE;

    @Column(name = "sale_time", nullable = false)
    private LocalDateTime saleTime = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Column(name = "created_by")
    private Long createdBy;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SaleItem> items = new ArrayList<>();
}