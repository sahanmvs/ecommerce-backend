package com.mvs.inventory_service.dto;

import com.mvs.inventory_service.model.Inventory;
import lombok.Data;

@Data
public class InventoryDto {
    private String id;
    private String productId;
    private int quantity;
    private int reserved;

    public static InventoryDto init(Inventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProductId());
        dto.setQuantity(inventory.getQuantity());
        dto.setReserved(inventory.getReserved());
        return dto;
    }
}
