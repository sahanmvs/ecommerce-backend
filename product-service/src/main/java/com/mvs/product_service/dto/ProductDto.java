package com.mvs.product_service.dto;

import com.mvs.product_service.model.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private String category;
    private String status;

    public static ProductDto init(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setStock(product.getStock());
        productDto.setCategory(product.getCategory());
        productDto.setStatus(product.getStatus());
        return productDto;
    }

    public static List<ProductDto> init(List<Product> products) {
        return products.stream().map(ProductDto::init).collect(Collectors.toList());
    }
}
