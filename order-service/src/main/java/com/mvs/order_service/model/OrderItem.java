package com.mvs.order_service.model;

import lombok.Data;

@Data
public class OrderItem {
    private String productId;
    private int quantity;
    private long price;
}
