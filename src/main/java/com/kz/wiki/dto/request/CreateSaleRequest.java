package com.kz.wiki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateSaleRequest {
    private Long storeId;
    private Long customerId;
    private Long sellerId;
    private Boolean useWholesalePrices = false;
    
    @NotEmpty(message = "Sale must have at least one item")
    @Valid
    private List<SaleItemRequest> items;
    
    private BigDecimal subtotal;
    private BigDecimal discount;
    private String discountType; // percentage, fixed
    private String discountCode;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private List<PaymentMethodRequest> paymentMethods;
    private List<String> giftCardCodes;
    private Integer loyaltyPointsUsed = 0;
    private BigDecimal debtPaymentAmount = BigDecimal.ZERO;
    private String note;
    private Boolean isDraft = false;
    private Boolean isDeferred = false;

    @Data
    public static class SaleItemRequest {
        @jakarta.validation.constraints.NotNull(message = "Product ID is required")
        private Long productId;

        @jakarta.validation.constraints.NotNull(message = "Quantity is required")
        @jakarta.validation.constraints.Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        private BigDecimal price;
        private BigDecimal wholesalePrice;
        private BigDecimal discount = BigDecimal.ZERO;
        private String discountType; // percentage, fixed
    }
    
    @Data
    public static class PaymentMethodRequest {
        private String method;
        private BigDecimal amount;
    }
}





