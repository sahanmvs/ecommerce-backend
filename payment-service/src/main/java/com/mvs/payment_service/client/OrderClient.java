package com.mvs.payment_service.client;

import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.BadRequestException;
import com.mvs.payment_service.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class OrderClient {
    private final WebClient.Builder webClientBuilder;

    public OrderResponse getOrder(String orderId, String userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://order-service/orders/{id}", orderId)
                .header("X-User-Id", userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new BadRequestException(ExType.ORDER_NOT_FOUND, "order not found")))
                .bodyToMono(OrderResponse.class)
                .block();
    }
}
