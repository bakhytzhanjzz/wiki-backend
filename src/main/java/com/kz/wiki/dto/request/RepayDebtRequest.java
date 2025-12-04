package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepayDebtRequest {
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private String paymentMethod;
    
    private String notes;
}

