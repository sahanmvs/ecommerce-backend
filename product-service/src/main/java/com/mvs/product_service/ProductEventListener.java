package com.mvs.product_service;

import com.mvs.common_module.events.InventoryUpdatedEvent;
import com.mvs.common_module.events.KafkaTopics;
import com.mvs.product_service.model.Product;
import com.mvs.product_service.repository.ProductRepository;
import com.mvs.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEventListener {
    private final ProductService productService;
    private final ProductRepository productRepository;

    @KafkaListener(topics = KafkaTopics.INVENTORY_UPDATED, groupId = "product-group")
    public void handleInventoryUpdatedEvent(InventoryUpdatedEvent event) {
        log.info("receive inventory updated event: {}", event);
        Product product = productService.getProduct(event.getProductId());
        if (product.getStockUpdatedAt() == null || event.getTimestamp().isAfter(product.getStockUpdatedAt())) {
            log.info("updating stock, productId = {}", event.getProductId());
            product.setStock(event.getQuantity());
            product.setStockUpdatedAt(Instant.now());
            productRepository.save(product);
        }
    }
}
