package com.mvs.inventory_service.dto;

import com.mvs.inventory_service.enums.StockOperation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class StockAdjustmentRequest {
    @NotNull(message = "operation required")
    private StockOperation operation;

    @NotNull(message = "quantity required")
    @Positive(message = "quantity must be greater than zero")
    private Integer quantity;
}
