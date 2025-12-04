package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateLoyaltyProgramRequest {
    @NotBlank(message = "Type is required")
    private String type; // discount, points
    
    @NotBlank(message = "Name is required")
    private String name;
}

