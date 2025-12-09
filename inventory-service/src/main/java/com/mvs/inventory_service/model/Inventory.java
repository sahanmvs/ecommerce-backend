package com.mvs.inventory_service.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("inventories")
@Data
public class Inventory {
    @Id
    private String id;
    private String productId;
    private int quantity;
    private int reserved; // if pre-orders are handled
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public boolean isInStock(int qty) {
        return qty <= quantity - reserved;
    }
}
