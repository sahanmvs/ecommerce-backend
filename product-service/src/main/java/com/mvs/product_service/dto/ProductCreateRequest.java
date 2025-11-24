package com.mvs.product_service.dto;

import com.mvs.product_service.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class ProductCreateRequest {

    @NotBlank(message = "product name is required")
    private String name;
    private String description;

    @PositiveOrZero(message = "price can't be less than zero")
    @NotNull(message = "price can't be empty")
    private BigDecimal price;

    @NotEmpty(message = "At least one category is required")
    private List<String> categories;
}
