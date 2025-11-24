package com.mvs.cart_service.client;

import com.mvs.cart_service.dto.ProductResponse;
import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductClient {
    private final WebClient.Builder webClientBuilder;

    public ProductResponse getProduct(String productId) {
        return webClientBuilder.build()
                .get()
                .uri("http://product-service/api/product/{id}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new BadRequestException(ExType.PRODUCT_NOT_FOUND, "product not found")))
                .bodyToMono(ProductResponse.class)
                .block();
    }
}
