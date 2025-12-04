package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateClientDebtRequest {
    @NotNull(message = "Client ID is required")
    private Long clientId;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    private LocalDate dueDate;
    private Long storeId;
    private String paymentType;
    private String notes;
}


