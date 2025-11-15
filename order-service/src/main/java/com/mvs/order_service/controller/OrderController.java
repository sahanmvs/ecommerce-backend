package com.mvs.order_service.controller;

import com.mvs.order_service.service.OrderService;
import dto.CreateOrderRequest;
import dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    public ResponseEntity<OrderDto> createOrder(CreateOrderRequest orderRequest) {
        return ResponseEntity.ok(OrderDto.init(orderService.createOrder(orderRequest)));
    }
}
