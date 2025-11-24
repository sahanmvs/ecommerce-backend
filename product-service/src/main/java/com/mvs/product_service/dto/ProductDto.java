package com.mvs.product_service.dto;

import com.mvs.product_service.enums.ProductStatus;
import com.mvs.product_service.model.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private List<String> categories;
    private ProductStatus status;
    private Instant stockUpdatedAt;

    /*public static ProductDto init(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setStock(product.getStock());
        productDto.setCategoryId(product.getCategoryId());
        productDto.setStatus(product.getStatus());
        return productDto;
    }

    public static List<ProductDto> init(List<Product> products) {
        return products.stream().map(ProductDto::init).collect(Collectors.toList());
    }*/
}
