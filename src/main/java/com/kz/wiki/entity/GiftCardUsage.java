package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_card_usages")
@Getter
@Setter
public class GiftCardUsage extends BaseTenantEntity {

    @Column(name = "gift_card_id", nullable = false)
    private Long giftCardId;

    @Column(name = "sale_id", nullable = false)
    private Long saleId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

