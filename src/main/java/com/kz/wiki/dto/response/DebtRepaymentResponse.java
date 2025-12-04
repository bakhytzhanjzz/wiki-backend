package com.kz.wiki.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DebtRepaymentResponse {
    private Long id;
    private Long debtId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private Long userId;
    private String userName;
    private String notes;
    private LocalDateTime createdAt;
}


