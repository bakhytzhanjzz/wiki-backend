package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SendSmsToDebtorsRequest {
    @NotNull(message = "Debt IDs are required")
    private List<Long> debtIds;
    
    @NotBlank(message = "Message is required")
    private String message;
}


