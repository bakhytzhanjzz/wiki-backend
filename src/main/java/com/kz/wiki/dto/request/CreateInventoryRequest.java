package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInventoryRequest {
    @NotBlank(message = "Inventory name is required")
    private String name;

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotBlank(message = "Type is required")
    private String type; // full, partial
}




