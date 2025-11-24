package com.mvs.order_service.dto;

import com.mvs.order_service.enums.OrderStatus;
import com.mvs.order_service.model.Order;
import com.mvs.order_service.model.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderDto {
    private String id;
    private String userId;
    private List<OrderItem> orderItems;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public static OrderDto init(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUserId());
        orderDto.setOrderItems(order.getItems());
        orderDto.setTotalAmount(order.getTotalPrice());
        orderDto.setStatus(order.getStatus());
        orderDto.setCreatedAt(order.getCreatedAt());
        orderDto.setUpdatedAt(order.getUpdatedAt());
        return orderDto;
    }
}
