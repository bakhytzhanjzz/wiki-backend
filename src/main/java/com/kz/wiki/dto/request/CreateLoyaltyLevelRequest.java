package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateLoyaltyLevelRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Purchase amount is required")
    private BigDecimal purchaseAmount;
    
    @NotNull(message = "Discount is required")
    private Integer discount;
    
    @NotNull(message = "Order is required")
    private Integer order;
}


