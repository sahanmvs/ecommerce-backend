package com.mvs.order_service.dto;

import com.mvs.order_service.model.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "cart id is required")
    private String cartId;
}
