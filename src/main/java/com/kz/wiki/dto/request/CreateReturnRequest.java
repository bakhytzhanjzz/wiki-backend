package com.kz.wiki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateReturnRequest {
    @NotEmpty(message = "Return must have at least one item")
    @Valid
    private List<ReturnItemRequest> items;

    private String refundMethod;

    private String note;

    @Data
    public static class ReturnItemRequest {
        private Long saleItemId;
        private Integer quantity;
        private String reason;
    }
}




