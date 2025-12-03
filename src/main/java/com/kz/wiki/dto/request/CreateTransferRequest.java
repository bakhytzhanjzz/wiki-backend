package com.kz.wiki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateTransferRequest {
    @NotBlank(message = "Transfer name is required")
    private String name;

    @NotNull(message = "From store ID is required")
    private Long fromStoreId;

    @NotNull(message = "To store ID is required")
    private Long toStoreId;

    @NotEmpty(message = "Transfer must have at least one item")
    @Valid
    private List<TransferItemRequest> items;

    private Boolean fromFile = false;

    @Data
    public static class TransferItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}


