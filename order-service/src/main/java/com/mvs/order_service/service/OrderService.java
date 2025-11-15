package com.mvs.order_service.service;

import com.mvs.common_module.events.KafkaTopics;
import com.mvs.common_module.events.OrderCreatedEvent;
import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.NotFoundException;
import com.mvs.order_service.model.Order;
import com.mvs.order_service.repository.OrderRepository;
import dto.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public Order createOrder(CreateOrderRequest orderRequest) {
        log.info("Create Order");
        long totalCents = orderRequest.getItems().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .userId(orderRequest.getUserId())
                .status(Order.OrderStatus.CREATED)
                .orderItems(orderRequest.getItems())
                .totalAmount(BigDecimal.valueOf(totalCents, 2))
                .build();
        Order saved = orderRepository.save(order);

        List<OrderCreatedEvent.OrderItem> items = new ArrayList<>();
        saved.getOrderItems().forEach(orderItem -> {
            OrderCreatedEvent.OrderItem item = new OrderCreatedEvent.OrderItem();
            item.setQuantity(orderItem.getQuantity());
            item.setPrice(orderItem.getPrice());
            items.add(item);
        });
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(saved.getId())
                .userId(saved.getUserId())
                .status(saved.getStatus())
                .totalAmount(saved.getTotalAmount())
                .orderItems(items)
                .build();
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event);

        return saved;
    }

    public Order getOrderById(String orderId) {
        log.info("Get Order by Id: {}", orderId);
        Optional<Order> opOrder = orderRepository.findById(orderId);
        if (opOrder.isEmpty()) throw new NotFoundException(ExType.ORDER_NOT_FOUND , "Order not found for id = "+orderId);
        return opOrder.get();
    }
}
