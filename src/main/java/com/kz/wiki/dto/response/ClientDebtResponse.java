package com.kz.wiki.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClientDebtResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private String clientPhone;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private String status;
    private LocalDateTime issueDate;
    private LocalDate dueDate;
    private Long storeId;
    private String storeName;
    private Long userId;
    private String userName;
    private String paymentType;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


