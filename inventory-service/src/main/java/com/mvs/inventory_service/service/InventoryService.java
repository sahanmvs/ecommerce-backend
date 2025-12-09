package com.mvs.inventory_service.service;

import com.mvs.common_module.events.InventoryUpdatedEvent;
import com.mvs.common_module.events.KafkaTopics;
import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.ConflictException;
import com.mvs.common_module.exceptions.exs.NotFoundException;
import com.mvs.inventory_service.model.Inventory;
import com.mvs.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Inventory getByProductId(String productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException(ExType.INVENTORY_NOT_FOUND, "Inventory not found for productId: " + productId));
    }

    public boolean isAvailable(String productId, int qty) {
        log.info("Checking availability for productId: {}, qty: {}", productId, qty);
        Inventory inventory = getByProductId(productId);
        return inventory.isInStock(qty);
    }

    public Inventory reduceStock(String productId, int qty) {
        log.info("Reduce stock for productId: {}, qty: {}", productId, qty);
        Inventory inventory = getByProductId(productId);
        if (inventory.isInStock(qty)) {
            inventory.setQuantity(inventory.getQuantity() - qty);
            Inventory saved = inventoryRepository.save(inventory);
            InventoryUpdatedEvent event = new InventoryUpdatedEvent(
                    saved.getQuantity(),
                    productId,
                    Instant.now()
            );
            kafkaTemplate.send(KafkaTopics.INVENTORY_UPDATED, event);
            return saved;
        } else {
            throw new ConflictException(ExType.INSUFFICIENT_STOCK, "Insufficient stock for product: " + productId);
        }
    }

    public Inventory increaseStock(String productId, int qty) {
        log.info("Increase stock for productId: {}, qty: {}", productId, qty);
        Inventory inventory = getByProductId(productId);
        inventory.setQuantity(inventory.getQuantity() + qty);
        Inventory saved = inventoryRepository.save(inventory);
        InventoryUpdatedEvent event = new InventoryUpdatedEvent(
                saved.getQuantity(),
                productId,
                Instant.now()
        );
        kafkaTemplate.send(KafkaTopics.INVENTORY_UPDATED, event);
        return saved;
    }
}
