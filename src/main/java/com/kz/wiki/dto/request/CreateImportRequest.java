package com.kz.wiki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateImportRequest {
    @NotBlank(message = "Import name is required")
    private String name;

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotEmpty(message = "Import must have at least one item")
    @Valid
    private List<ImportItemRequest> items;

    @Data
    public static class ImportItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        @NotNull(message = "Price is required")
        private java.math.BigDecimal price;
    }
}



