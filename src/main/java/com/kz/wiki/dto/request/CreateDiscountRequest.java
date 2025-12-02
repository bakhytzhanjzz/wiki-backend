package com.kz.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateDiscountRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Type is required")
    private String type; // percentage, fixed, free_shipping

    @NotNull(message = "Value is required")
    private BigDecimal value;

    private BigDecimal minPurchaseAmount;

    private BigDecimal maxDiscountAmount;

    private String applicableTo = "all"; // all, products, categories

    private List<Long> applicableProductIds;

    private List<Long> applicableCategoryIds;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer usageLimit;

    private Boolean isActive = true;
}

