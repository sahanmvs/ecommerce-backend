package com.mvs.cart_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemToCartRequest {
    @NotNull(message = "product id can't be null")
    private String productId;
}
