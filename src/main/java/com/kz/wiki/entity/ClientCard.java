package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "client_cards")
@Getter
@Setter
public class ClientCard extends BaseTenantEntity {

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private String type; // passport, id_card, etc.

    @Column(nullable = false)
    private String number;

    @Column(name = "file_url")
    private String fileUrl;
}


