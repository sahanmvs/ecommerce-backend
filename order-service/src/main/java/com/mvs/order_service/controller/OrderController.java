package com.mvs.order_service.controller;

import com.mvs.order_service.service.OrderService;
import com.mvs.order_service.dto.CreateOrderRequest;
import com.mvs.order_service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody @Validated CreateOrderRequest orderRequest,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(OrderDto.init(orderService.createOrder(orderRequest.getCartId(), userId)));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(
            @PathVariable String orderId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(OrderDto.init(orderService.cancelOrder(orderId, userId)));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String orderId, @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(OrderDto.init(orderService.getUserOrder(userId, orderId)));
    }
}
