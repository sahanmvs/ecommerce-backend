package com.mvs.payment_service.service;

import com.mvs.common_module.events.KafkaTopics;
import com.mvs.common_module.events.PaymentFailedEvent;
import com.mvs.common_module.events.PaymentSuccessEvent;
import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.ConflictException;
import com.mvs.common_module.exceptions.exs.NotFoundException;
import com.mvs.payment_service.client.OrderClient;
import com.mvs.payment_service.dto.CheckoutSession;
import com.mvs.payment_service.dto.OrderResponse;
import com.mvs.payment_service.dto.PaymentResponse;
import com.mvs.payment_service.enums.PaymentStatus;
import com.mvs.payment_service.model.PaymentRecord;
import com.mvs.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentGateway paymentGateway;
    private final OrderClient orderClient;
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public PaymentResponse createCheckout(String userId, String orderId) {
        log.info("create checkout user {} order {}", userId, orderId);
        OrderResponse order = orderClient.getOrder(orderId, userId);
        if (!order.getStatus().equals("PENDING_PAYMENT")) {
            throw new ConflictException(ExType.INVALID_ORDER_STATUS, "invalid order status " + order.getStatus());
        }
        Optional<PaymentRecord> opPaymentRecord = paymentRepository.findPaymentRecordByOrderIdAndStatus(orderId, PaymentStatus.PENDING);
        if (opPaymentRecord.isPresent()) {
            return new PaymentResponse(orderId, opPaymentRecord.get().getId(), opPaymentRecord.get().getMetadata().get("checkout_url"));
        }
        
        CheckoutSession checkoutSession = paymentGateway.createCheckoutSession(order);
        PaymentRecord record = PaymentRecord.builder()
                .orderId(order.getId())
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .metadata(Map.of("checkout_url", checkoutSession.getUrl()))
                .build();
        record = paymentRepository.save(record);
        return new PaymentResponse(order.getId(), record.getId(), checkoutSession.getUrl());
    }

    public void handleWebhookEvent(Map<String, String> headers, String payload) {
        paymentGateway.handleWebhookEvent(headers, payload)
                .ifPresent(paymentResult -> {
                    PaymentRecord paymentRecord = paymentRepository.findPaymentRecordByOrderId(paymentResult.getOrderId())
                            .orElseThrow(() -> new NotFoundException(
                                            ExType.PAYMENT_RECORD_NOT_FOUND,
                                            "payment record not found for orderId " + paymentResult.getOrderId()
                                    )
                            );
                    paymentRecord.setStatus(paymentResult.getPaymentStatus());
                    paymentRepository.save(paymentRecord);

                    if (paymentResult.getPaymentStatus().equals(PaymentStatus.SUCCEEDED)) {
                        PaymentSuccessEvent successEvent = new PaymentSuccessEvent(paymentResult.getOrderId(), paymentResult.getPaymentStatus().toString());
                        kafkaTemplate.send(KafkaTopics.PAYMENT_SUCCESS, successEvent);
                    } else if (paymentResult.getPaymentStatus().equals(PaymentStatus.FAILED)) {
                        PaymentFailedEvent failedEvent = new PaymentFailedEvent(paymentResult.getOrderId(), paymentResult.getPaymentStatus().toString());
                        kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, failedEvent);
                    }
                });
    }
}
