package com.mvs.payment_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderResponse {
    private String id;
    private String userId;
    private List<OrderItem> orderItems;
    private BigDecimal totalAmount;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    public static class OrderItem {
        private String productId;
        private int quantity;
        private BigDecimal price;
    }
}
