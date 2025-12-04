package com.kz.wiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transfers")
@Getter
@Setter
public class Transfer extends BaseTenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "from_store_id", nullable = false)
    private Long fromStoreId;

    @Column(name = "to_store_id", nullable = false)
    private Long toStoreId;

    @Column(nullable = false)
    private String status = "pending"; // pending, in_transit, received, cancelled

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "received_by")
    private Long receivedBy;

    @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferItem> items = new ArrayList<>();
}



