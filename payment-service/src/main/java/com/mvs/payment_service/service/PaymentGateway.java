package com.mvs.payment_service.service;

import com.mvs.payment_service.dto.CheckoutSession;
import com.mvs.payment_service.dto.OrderResponse;
import com.mvs.payment_service.dto.PaymentResult;

import java.util.Map;
import java.util.Optional;

public interface PaymentGateway {

    CheckoutSession createCheckoutSession(OrderResponse orderResponse);

    Optional<PaymentResult> handleWebhookEvent(Map<String, String> headers, String payload);
}
