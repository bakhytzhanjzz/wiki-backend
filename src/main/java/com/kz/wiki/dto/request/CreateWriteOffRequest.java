package com.kz.wiki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateWriteOffRequest {
    @NotBlank(message = "Write-off name is required")
    private String name;

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotBlank(message = "Type is required")
    private String type; // damaged, expired, lost, other

    private String reason;

    @NotEmpty(message = "Write-off must have at least one item")
    @Valid
    private List<WriteOffItemRequest> items;

    private Boolean fromFile = false;

    @Data
    public static class WriteOffItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}



