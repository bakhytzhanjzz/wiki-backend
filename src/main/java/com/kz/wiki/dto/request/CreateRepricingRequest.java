package com.kz.wiki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateRepricingRequest {
    @NotBlank(message = "Repricing name is required")
    private String name;

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotBlank(message = "Type is required")
    private String type; // markup, discount, fixed

    @NotEmpty(message = "Repricing must have at least one item")
    @Valid
    private List<RepricingItemRequest> items;

    private Boolean fromFile = false;

    @Data
    public static class RepricingItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "New price is required")
        private java.math.BigDecimal newPrice;
    }
}



