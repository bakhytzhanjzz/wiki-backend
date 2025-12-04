package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePaymentMethodRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    private String icon;

    private Boolean isActive = true;

    private Integer sortOrder = 0;
}




