package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClientTagRequest {
    @NotBlank(message = "Tag name is required")
    private String name;
    
    private String type = "manual"; // manual, auto
    private String status = "open"; // open, closed
    private String description;
}

