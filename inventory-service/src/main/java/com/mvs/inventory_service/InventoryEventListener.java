package com.mvs.inventory_service;

import com.mvs.common_module.events.*;
import com.mvs.inventory_service.model.Inventory;
import com.mvs.inventory_service.repository.InventoryRepository;
import com.mvs.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventListener {
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "inventory-group")
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        log.info("receive product created event: {}", event);
        Inventory inventory = new Inventory();
        inventory.setProductId(event.getProductId());
        inventoryRepository.save(inventory);
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "inventory-group")
    public void handle(OrderCreatedEvent event) {
        log.info("receive order created event: {}", event);
        try {
            boolean allItemsInStock = event.getOrderItems().stream().allMatch(item -> {
                Inventory inventory = inventoryService.getByProductId(item.getProductId());
                return inventory.isInStock(item.getQuantity());
            });

            if (allItemsInStock) {
                log.info("All items in stock");
                event.getOrderItems().forEach(item -> {
                    inventoryService.reduceStock(item.getProductId(), item.getQuantity());
                });
                StockReservedEvent reservedEvent = StockReservedEvent.builder()
                        .orderId(event.getOrderId())
                        .items(event.getOrderItems())
                        .build();
                kafkaTemplate.send(KafkaTopics.STOCK_RESERVED, reservedEvent);
            } else {
                log.info("items not in stock");
                this.publishStockRejectedEvent(event.getOrderId(), "Insufficient stock");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.publishStockRejectedEvent(event.getOrderId(), e.getMessage());
        }
    }

    private void publishStockRejectedEvent(String orderId, String reason) {
        StockRejectedEvent rejectedEvent = StockRejectedEvent.builder()
                .orderId(orderId)
                .reason(reason)
                .build();
        kafkaTemplate.send(KafkaTopics.STOCK_REJECTED, rejectedEvent);
    }
}
