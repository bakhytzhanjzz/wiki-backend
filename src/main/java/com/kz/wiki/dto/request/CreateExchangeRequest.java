package com.kz.wiki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateExchangeRequest {
    @NotEmpty(message = "Exchange must have return items")
    @Valid
    private List<ReturnItemRequest> returnItems;

    @NotEmpty(message = "Exchange must have new items")
    @Valid
    private List<NewItemRequest> newItems;

    private BigDecimal differenceAmount;

    private String paymentMethod;

    private String note;

    @Data
    public static class ReturnItemRequest {
        private Long saleItemId;
        private Integer quantity;
        private String reason;
    }

    @Data
    public static class NewItemRequest {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
    }
}




