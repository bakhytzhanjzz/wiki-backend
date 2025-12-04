package com.kz.wiki.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientTagResponse {
    private Long id;
    private String name;
    private String type;
    private String status;
    private String description;
    private Integer clientsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


