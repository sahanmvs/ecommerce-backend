package com.mvs.cart_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
