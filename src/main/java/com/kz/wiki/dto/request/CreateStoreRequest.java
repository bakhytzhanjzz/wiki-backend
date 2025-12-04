package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateStoreRequest {
    @NotBlank(message = "Store name is required")
    @Size(min = 1, max = 200, message = "Store name must be between 1 and 200 characters")
    private String name;

    private String address;
}



