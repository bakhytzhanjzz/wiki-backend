package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateGiftCardRequest {
    @NotBlank(message = "Type is required")
    private String type; // certificate, voucher

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private LocalDateTime expiresAt;

    private Long storeId;

    private String note;

    private String status;
}


