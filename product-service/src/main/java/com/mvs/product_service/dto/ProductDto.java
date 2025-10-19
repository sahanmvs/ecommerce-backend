package com.mvs.product_service.dto;

import com.mvs.product_service.model.Product;
import lombok.Data;

@Data
public class ProductDto {
    private String name;
    private String description;
    private long price;
    private int stock;
    private String category;

    public static ProductDto init(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setStock(product.getStock());
        productDto.setCategory(product.getCategory());
        return productDto;
    }
}
