package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClientGroupRequest {
    @NotBlank(message = "Group name is required")
    private String name;
    
    private Integer discountPercent = 0;
    private String discountApplication; // retail_only, wholesale_only, both
    private String status = "open"; // open, closed
    private String description;
}


