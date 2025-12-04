package com.kz.wiki.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoyaltyLevelResponse {
    private Long id;
    private String name;
    private BigDecimal purchaseAmount;
    private Integer discount;
    private Integer order;
}


