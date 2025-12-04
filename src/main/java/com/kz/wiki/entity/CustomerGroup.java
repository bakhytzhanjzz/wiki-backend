package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_groups", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"customer_id", "group_id", "tenant_id"})
})
@Getter
@Setter
public class CustomerGroup extends BaseTenantEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;
}


