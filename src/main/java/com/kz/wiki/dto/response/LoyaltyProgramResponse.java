package com.kz.wiki.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LoyaltyProgramResponse {
    private Long id;
    private String type;
    private String name;
    private List<LoyaltyLevelResponse> levels;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


