package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "code"})
})
@Getter
@Setter
public class Discount extends BaseTenantEntity {

    @Column(nullable = false, unique = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // percentage, fixed, free_shipping

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @Column(name = "min_purchase_amount", precision = 19, scale = 2)
    private BigDecimal minPurchaseAmount;

    @Column(name = "max_discount_amount", precision = 19, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "applicable_to")
    private String applicableTo = "all"; // all, products, categories

    @ElementCollection
    @CollectionTable(name = "discount_applicable_products", joinColumns = @JoinColumn(name = "discount_id"))
    @Column(name = "product_id")
    private List<Long> applicableProductIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "discount_applicable_categories", joinColumns = @JoinColumn(name = "discount_id"))
    @Column(name = "category_id")
    private List<Long> applicableCategoryIds = new ArrayList<>();

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}


