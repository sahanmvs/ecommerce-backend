package com.mvs.inventory_service;

import com.mvs.common_module.events.KafkaTopics;
import com.mvs.common_module.events.ProductCreatedEvent;
import com.mvs.inventory_service.model.Inventory;
import com.mvs.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventListener {
    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "inventory-group")
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        log.info("receive product created event: " + event);
        Inventory inventory = new Inventory();
        inventory.setProductId(event.getProductId());
        inventoryRepository.save(inventory);
    }
}
