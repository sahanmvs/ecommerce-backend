package com.mvs.order_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class CartResponse {
    private String id;
    private List<CartItemResponse> items;
    private Instant createdAt;
    private Instant updatedAt;
    private BigDecimal totalPrice;

    public boolean isCartEmpty() {
        return items == null || items.isEmpty();
    }
}
