package com.mvs.inventory_service.service;

import com.mvs.inventory_service.model.Inventory;
import com.mvs.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public Inventory getByProductId(String productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for productId: " + productId));
    }

    public boolean isAvailable(String productId, int qty) {
        log.info("Checking availability for productId: " + productId + ", qty: " + qty);
        Inventory inventory = getByProductId(productId);
        return inventory.isInStock(qty);
    }

    public void reduceStock(String productId, int qty) {
        log.info("Reduce stock for productId: " + productId + ", qty: " + qty);
        Inventory inventory = getByProductId(productId);
        if (inventory.isInStock(qty)) {
            inventory.setQuantity(inventory.getQuantity() - qty);
            inventoryRepository.save(inventory);
        } else {
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }
    }

    public void increaseStock(String productId, int qty) {
        log.info("Increase stock for productId: " + productId + ", qty: " + qty);
        Inventory inventory = getByProductId(productId);
        inventory.setQuantity(inventory.getQuantity() + qty);
        inventoryRepository.save(inventory);
    }
}
