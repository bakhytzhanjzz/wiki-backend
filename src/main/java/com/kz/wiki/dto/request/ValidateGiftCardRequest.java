package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateGiftCardRequest {
    @NotBlank(message = "Code is required")
    private String code;
}



