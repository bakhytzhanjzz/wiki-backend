package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BulkRepayDebtsRequest {
    @NotNull(message = "Debt IDs are required")
    private List<Long> debtIds;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private String paymentMethod;
    
    private String notes;
}


