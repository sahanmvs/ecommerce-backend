package com.mvs.cart_service.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItem {
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }
}
