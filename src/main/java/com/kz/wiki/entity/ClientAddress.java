package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "client_addresses")
@Getter
@Setter
public class ClientAddress extends BaseTenantEntity {

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private String address;

    private String city;

    private String region;

    @Column(name = "postal_code")
    private String postalCode;
}


