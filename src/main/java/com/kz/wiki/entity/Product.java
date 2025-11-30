package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "sku"})
})
@Getter
@Setter
public class Product extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = false) // unique within tenant — enforced by composite constraint
    private String sku;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_qty", nullable = false)
    private Integer stockQty = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}