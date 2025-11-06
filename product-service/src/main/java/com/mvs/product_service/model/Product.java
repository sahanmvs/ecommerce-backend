package com.mvs.product_service.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "products")
@Data
@Builder
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private long price;
    private int stock;
    private String category;
    private String status;
    private Instant stockUpdatedAt;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    @Data
    public static class ProductStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String DELETED = "DELETED";
        public static final String ARCHIVED = "ARCHIVED";
        public static final String OUT_OF_STOCK = "OUT_OF_STOCK";
    }
}

