package com.mvs.order_service;

import com.mvs.common_module.events.KafkaTopics;
import com.mvs.common_module.events.StockRejectedEvent;
import com.mvs.common_module.events.StockReservedEvent;
import com.mvs.order_service.model.Order;
import com.mvs.order_service.repository.OrderRepository;
import com.mvs.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = KafkaTopics.STOCK_RESERVED, groupId = "order-group")
    public void handle(StockReservedEvent event) {
        log.info("Received stock reserved event: {}", event);
        Order order = orderService.getOrderById(event.getOrderId());
        order.setStatus(Order.OrderStatus.RESERVED);
        orderRepository.save(order);
    }

    @KafkaListener(topics = KafkaTopics.STOCK_REJECTED, groupId = "order-group")
    public void handle(StockRejectedEvent event) {
        log.info("Received stock rejected event: {}", event);
        Order order = orderService.getOrderById(event.getOrderId());
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // todo : handle payment events
}
