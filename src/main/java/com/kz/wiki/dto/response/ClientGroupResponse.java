package com.kz.wiki.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientGroupResponse {
    private Long id;
    private String name;
    private Integer discountPercent;
    private String discountApplication;
    private String status;
    private String description;
    private Integer clientsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


