package com.mvs.order_service.client;

import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.BadRequestException;
import com.mvs.order_service.dto.ProductResponse;
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
                .uri("http://product-service/api/product/" + productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new BadRequestException(ExType.PRODUCT_NOT_FOUND, "product not found"))) // todo: product status handle in prod service
                .bodyToMono(ProductResponse.class)
                .block();
    }

}
