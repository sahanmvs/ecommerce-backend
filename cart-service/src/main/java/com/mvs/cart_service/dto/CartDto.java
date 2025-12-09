package com.mvs.cart_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class CartDto {
    private String id;
    private List<CartItemDto> items;
    private Instant createdAt;
    private Instant updatedAt;
    private BigDecimal totalPrice;
}
