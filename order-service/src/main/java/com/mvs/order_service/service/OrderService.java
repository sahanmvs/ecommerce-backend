package com.mvs.order_service.service;

import com.mvs.common_module.events.KafkaTopics;
import com.mvs.common_module.events.OrderCancelledEvent;
import com.mvs.common_module.events.OrderCreatedEvent;
import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.BadRequestException;
import com.mvs.common_module.exceptions.exs.ConflictException;
import com.mvs.common_module.exceptions.exs.NotFoundException;
import com.mvs.common_module.exceptions.exs.UnauthorizedException;
import com.mvs.order_service.client.CartClient;
import com.mvs.order_service.client.ProductClient;
import com.mvs.order_service.dto.CartItemResponse;
import com.mvs.order_service.dto.CartResponse;
import com.mvs.order_service.dto.ProductResponse;
import com.mvs.order_service.enums.CancelReason;
import com.mvs.order_service.enums.OrderStatus;
import com.mvs.order_service.model.Order;
import com.mvs.order_service.model.OrderItem;
import com.mvs.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductClient productClient;
    private final CartClient cartClient;

    public Order createOrder(String cartId, String userId) {
        log.info("Create Order");
        CartResponse cart = cartClient.getCart(cartId);
        if (cart.isCartEmpty()) throw new BadRequestException(ExType.EMPTY_CART, "Cart is empty");
        Order order = new Order();

        List<CartItemResponse> priceChanges = new ArrayList<>();

        cart.getItems().forEach(item -> {
            ProductResponse product = productClient.getProduct(item.getProductId());
            if (product.getPrice().compareTo(item.getUnitPrice()) != 0) {
                priceChanges.add(item);
            }
            OrderItem orderItem = new OrderItem(item.getProductId(), item.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.getItems().add(orderItem);
        });

        if (!priceChanges.isEmpty()) {
            cartClient.updateCartItemPrices(cartId, priceChanges);
            throw new ConflictException(ExType.PRICE_MISMATCH, "price mismatch please review your cart");
        }

        order.setOrderId(UUID.randomUUID().toString()); // todo: implement counter
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(order.getTotalPrice());
        order.setExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES)); // todo: from yaml
        Order saved = orderRepository.save(order);

        cartClient.clearCart(cartId);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(saved.getId())
                .userId(saved.getUserId())
                .status(saved.getStatus().toString())
                .totalAmount(saved.getTotalPrice())
                .orderItems(getEventOrderItems(saved.getItems()))
                .build();
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event);

        return saved;
    }

    public Order getOrderById(String id) {
        log.info("Get Order by Id {}", id);
        Optional<Order> opOrder = orderRepository.findById(id);
        if (opOrder.isEmpty()) throw new NotFoundException(ExType.ORDER_NOT_FOUND , "Order not found for id = "+id);
        return opOrder.get();
    }

    public Order getOrderByOrderId(String orderId) {
        log.info("Get Order by Order Id {}", orderId);
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() ->new NotFoundException(ExType.ORDER_NOT_FOUND, "order not found for orderId = "+orderId));
    }

    public Order cancelOrder(String orderId, String userId) {
        log.info("Cancel Order {} userId {}", orderId, userId);
        Order order = getOrderByOrderId(orderId);
        if (!order.getUserId().equals(userId)) throw new UnauthorizedException(ExType.UNAUTHORIZED, "Unauthorized");

        if (order.getStatus().equals(OrderStatus.PENDING) || order.getStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setCancelReason(CancelReason.USER_CANCELLED);
            Order saved = orderRepository.save(order);

            OrderCancelledEvent cancelledEvent = OrderCancelledEvent.builder()
                    .orderId(saved.getId())
                    .items(getEventOrderItems(saved.getItems()))
                    .build();
            kafkaTemplate.send(KafkaTopics.ORDER_CANCELLED, cancelledEvent);
            return saved;
        } else {
            throw new ConflictException(ExType.INVALID_STATUS, "can't perform this action, order status = "+ order.getStatus());
        }
    }

    public List<OrderCreatedEvent.OrderItem> getEventOrderItems(List<OrderItem> orderItems) {
        List<OrderCreatedEvent.OrderItem> eventOrderItems = new ArrayList<>();
        orderItems.forEach(orderItem -> {
            OrderCreatedEvent.OrderItem item = new OrderCreatedEvent.OrderItem();
            item.setProductId(orderItem.getProductId());
            item.setQuantity(orderItem.getQuantity());
            item.setPrice(orderItem.getPrice().longValue()); // todo big decimal event?
            eventOrderItems.add(item);
        });
        return eventOrderItems;
    }
}
