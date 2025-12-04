package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "loyalty_levels")
@Getter
@Setter
public class LoyaltyLevel extends BaseTenantEntity {

    @Column(name = "loyalty_program_id", nullable = false)
    private Long loyaltyProgramId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loyalty_program_id", insertable = false, updatable = false)
    private LoyaltyProgram loyaltyProgram;

    @Column(nullable = false)
    private String name;

    @Column(name = "purchase_amount", precision = 19, scale = 2)
    private BigDecimal purchaseAmount = BigDecimal.ZERO;

    @Column(name = "discount")
    private Integer discount = 0;

    @Column(name = "level_order", nullable = false)
    private Integer order;
}


