package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "suppliers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "name"})
})
@Getter
@Setter
public class Supplier extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(name = "default_markup", precision = 5, scale = 2)
    private BigDecimal defaultMarkup;

    @Column(columnDefinition = "TEXT")
    private String note;
}




