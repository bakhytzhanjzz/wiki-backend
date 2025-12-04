package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ValidateDiscountRequest {
    @NotBlank(message = "Code is required")
    private String code;

    private Long customerId;

    @NotNull(message = "Items are required")
    private List<Item> items;

    @NotNull(message = "Subtotal is required")
    private BigDecimal subtotal;

    @Data
    public static class Item {
        @NotNull
        private Long productId;
        @NotNull
        private Integer quantity;
        @NotNull
        private BigDecimal price;
    }
}




