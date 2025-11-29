package com.mvs.payment_service.dto;

import com.mvs.payment_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentResult {
    private String orderId;
    private PaymentStatus paymentStatus;
}
