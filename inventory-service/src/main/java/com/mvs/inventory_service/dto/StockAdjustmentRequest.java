package com.mvs.inventory_service.dto;

import lombok.Data;

@Data
public class StockAdjustmentRequest {
    private String operation;
    private int quantity;
}
