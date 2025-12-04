package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCustomerRequest {
    @NotBlank(message = "Customer name is required")
    private String name;

    private String phone;

    private String email;

    private String cardNumber;

    private String notes;
}



