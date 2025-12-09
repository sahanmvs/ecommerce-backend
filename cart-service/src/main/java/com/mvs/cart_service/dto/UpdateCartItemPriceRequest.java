package com.mvs.cart_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCartItemPriceRequest {
    private String productId;
    private BigDecimal unitPrice;
}
