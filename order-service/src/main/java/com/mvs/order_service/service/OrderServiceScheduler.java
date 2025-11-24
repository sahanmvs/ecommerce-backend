package com.mvs.order_service.service;

import com.mvs.common_module.events.KafkaTopics;
import com.mvs.common_module.events.OrderExpiredEvent;
import com.mvs.order_service.enums.CancelReason;
import com.mvs.order_service.enums.OrderStatus;
import com.mvs.order_service.model.Order;
import com.mvs.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceScheduler {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedRate = 60000)
    public void expireUnpaidOrders() {
        log.info("expire unpaid orders");
        List<Order> orderList = orderRepository.findByStatusAndExpiresAtBefore(OrderStatus.PENDING_PAYMENT, Instant.now());
        orderList.forEach(order -> {
            order.setStatus(OrderStatus.EXPIRED);
            order.setCancelReason(CancelReason.SYSTEM_EXPIRED);
            orderRepository.save(order);

            OrderExpiredEvent event = OrderExpiredEvent.builder()
                    .orderId(order.getId())
                    .items(orderService.getEventOrderItems(order.getItems()))
                    .build();
            kafkaTemplate.send(KafkaTopics.ORDER_EXPIRED, event);
            log.info("order {} expired and event sent", order.getId());
        });
    }
}
