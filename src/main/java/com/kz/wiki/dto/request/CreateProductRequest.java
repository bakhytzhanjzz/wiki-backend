package com.kz.wiki.dto.request;

import com.kz.wiki.validation.ValidPrice;
import com.kz.wiki.validation.ValidSku;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 200, message = "Product name must be between 1 and 200 characters")
    private String name;

    @NotBlank(message = "SKU is required")
    @ValidSku(message = "SKU must be 3-50 characters, alphanumeric with dashes and underscores")
    private String sku;

    @NotNull(message = "Price is required")
    @ValidPrice(message = "Price must be positive and have at most 2 decimal places")
    private BigDecimal price;

    private Integer stockQty = 0;

    private Long categoryId;
}


