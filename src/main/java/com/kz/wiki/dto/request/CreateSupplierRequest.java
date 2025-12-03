package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSupplierRequest {
    @NotBlank(message = "Supplier name is required")
    @Size(min = 1, max = 200, message = "Supplier name must be between 1 and 200 characters")
    private String name;

    private String phone;

    private BigDecimal defaultMarkup;

    private String note;
}


