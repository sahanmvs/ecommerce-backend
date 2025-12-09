package com.mvs.payment_service.service;

import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.PaymentException;
import com.mvs.payment_service.client.ProductClient;
import com.mvs.payment_service.dto.*;
import com.mvs.payment_service.enums.PaymentStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.thoughtworks.xstream.io.StreamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StripePaymentGateway implements PaymentGateway {
    private final ProductClient productClient;

    @Value("${webUrl}")
    private String webUrl;

    @Value("${stripe.webhookSecret}")
    private String webhookSecret;

    @Override
    public CheckoutSession createCheckoutSession(OrderResponse order) {
        log.info("create stripe checkout session {}", order.getId());
        try {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(webUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(webUrl + "/checkout-cancel?orderId=" + order.getId())
                    .putMetadata("order_id", order.getId());

            order.getOrderItems().forEach(item -> {
                ProductResponse product = productClient.getProduct(item.getProductId());
                SessionCreateParams.LineItem lineItem = getLineItem(item, product);
                builder.addLineItem(lineItem);
            });

            SessionCreateParams params = builder.build();
            Session session = Session.create(params);
            return new CheckoutSession(session.getUrl());

        } catch (StripeException e) {
            log.error(e.getMessage());
            throw new PaymentException(ExType.PAYMENT_ERROR, e.getMessage());
        }
    }

    @Override
    public Optional<PaymentResult> handleWebhookEvent(Map<String, String> headers, String payload) {
        try {
            String signature = headers.get("stripe-signature");
            Event event = Webhook.constructEvent(payload, signature, webhookSecret);
            log.info("event {} {}", event.getType(), event.getId());
            // todo: idempotent (handle only once)
            return switch (event.getType()) {
                case "payment_intent.succeeded" ->
                    Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.SUCCEEDED));
                case "payment_intent.payment_failed" ->
                    Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.FAILED));
                default ->  Optional.empty();
            };

        } catch (SignatureVerificationException e) {
            log.error(e.getMessage());
            throw new PaymentException(ExType.PAYMENT_ERROR, e.getMessage());
        }
    }

    private static String extractOrderId(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new PaymentException(ExType.PAYMENT_ERROR, "stripe event deserialize error"));
        return paymentIntent.getMetadata().get("order_id");
    }

    private SessionCreateParams.LineItem getLineItem(OrderResponse.OrderItem item, ProductResponse product) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity((long) item.getQuantity())
                .setPriceData(getPriceData(item, product))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData getPriceData(OrderResponse.OrderItem item, ProductResponse product) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setUnitAmountDecimal(item.getPrice().multiply(BigDecimal.valueOf(100))) // cents
                .setCurrency("usd")
                .setProductData(getProductData(product))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData getProductData(ProductResponse product) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(product.getName())
                .setDescription(product.getDescription())
                .build();
    }


}
