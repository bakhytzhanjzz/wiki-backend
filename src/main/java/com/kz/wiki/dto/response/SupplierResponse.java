package com.kz.wiki.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {
    private Long id;
    private String name;
    private String phone;
    private BigDecimal defaultMarkup;
    private String note;
    private BigDecimal debtAmount;
    private BigDecimal ordersAmount;
    private BigDecimal paymentsAmount;
    private Integer productsCount;
}

