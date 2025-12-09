package com.mvs.order_service;

import com.mvs.common_module.events.*;
import com.mvs.order_service.enums.CancelReason;
import com.mvs.order_service.enums.OrderStatus;
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
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        orderRepository.save(order);
        // todo: payment request event
    }

    @KafkaListener(topics = KafkaTopics.STOCK_REJECTED, groupId = "order-group")
    public void handle(StockRejectedEvent event) {
        log.info("Received stock rejected event: {}", event);
        Order order = orderService.getOrderById(event.getOrderId());
        order.setStatus(OrderStatus.CANCELLED);
        order.getMetadata().put("stock_rejected_reason", event.getReason());
        order.setCancelReason(CancelReason.STOCK_REJECTED);
        orderRepository.save(order);
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_SUCCESS, groupId = "order-group")
    public void handle(PaymentSuccessEvent event) {
        log.info("Received payment success event: {}", event);
        Order order = orderService.getOrderById(event.getOrderId());
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "order-group")
    public void handle(PaymentFailedEvent event) {
        log.info("Received payment failed event: {}", event);
        Order order = orderService.getOrderById(event.getOrderId());
        order.setStatus(OrderStatus.FAILED); // todo : release stock
        orderRepository.save(order);
    }
}
