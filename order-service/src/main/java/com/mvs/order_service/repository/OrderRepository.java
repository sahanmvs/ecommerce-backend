package com.mvs.order_service.repository;

import com.mvs.order_service.enums.OrderStatus;
import com.mvs.order_service.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderId(String orderId);

    List<Order> findByStatusAndExpiresAtBefore(OrderStatus status, Instant time);
}
