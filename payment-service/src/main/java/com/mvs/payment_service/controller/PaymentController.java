package com.mvs.payment_service.controller;

import com.mvs.payment_service.dto.CheckoutRequest;
import com.mvs.payment_service.dto.PaymentResponse;
import com.mvs.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{orderId}/initialize")
    public PaymentResponse createCheckout(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable  String orderId
    ) {
        return paymentService.createCheckout(userId, orderId);
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestHeader Map<String, String> headers,
                              @RequestBody String payload) {
        paymentService.handleWebhookEvent(headers, payload);
    }
}
