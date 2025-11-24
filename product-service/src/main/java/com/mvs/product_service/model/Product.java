package com.mvs.product_service.model;

import com.mvs.product_service.enums.ProductStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "products")
@Data
@Builder
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private List<String> categories;
    private ProductStatus status;
    private Instant stockUpdatedAt;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}

