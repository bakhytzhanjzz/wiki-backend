package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_tags", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"customer_id", "tag_id", "tenant_id"})
})
@Getter
@Setter
public class CustomerTag extends BaseTenantEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;
}


