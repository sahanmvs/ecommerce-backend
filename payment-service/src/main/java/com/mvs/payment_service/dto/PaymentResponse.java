package com.mvs.payment_service.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String orderId;
    private String checkoutUrl;
    private String paymentId;

    public PaymentResponse(String orderId, String paymentId, String checkoutSessionUrl) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.checkoutUrl = checkoutSessionUrl;
    }
}
