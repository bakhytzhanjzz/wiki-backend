package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "name"})
})
@Getter
@Setter
public class Category extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;
}