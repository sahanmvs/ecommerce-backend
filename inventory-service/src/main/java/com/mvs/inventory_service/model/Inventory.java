package com.mvs.inventory_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("inventories")
@Data
public class Inventory {
    @Id
    private String id;
    private String productId;
    private int quantity;
    private int reserved; // if pre-orders are handled

    public boolean isInStock(int qty) {
        return qty <= quantity - reserved;
    }
}
