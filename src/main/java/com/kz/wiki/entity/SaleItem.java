package com.kz.wiki.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
@Getter
@Setter
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    @JsonBackReference
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal price;
    
    // Database also has a 'price' column that needs to be populated
    // This is a duplicate mapping - both unit_price and price should have the same value
    @Column(name = "price", precision = 19, scale = 2, nullable = false, insertable = true, updatable = true)
    private BigDecimal priceColumn;

    @Column(name = "wholesale_price", precision = 19, scale = 2)
    private BigDecimal wholesalePrice;

    @Column(name = "discount", precision = 19, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "discount_type")
    private String discountType; // percentage, fixed

    @Column(name = "total_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "type")
    private String type = "product"; // product, service, kit, certificate
}