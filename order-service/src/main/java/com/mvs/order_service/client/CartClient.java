package com.mvs.order_service.client;

import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.ExceptionType;
import com.mvs.common_module.exceptions.exs.BadRequestException;
import com.mvs.order_service.dto.CartItemResponse;
import com.mvs.order_service.dto.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartClient {
    private final WebClient.Builder webClientBuilder;

    public CartResponse getCart(String cartId) {
        return webClientBuilder.build()
                .get()
                .uri("http://cart-service/carts/" + cartId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new BadRequestException(ExType.CART_NOT_FOUND, "cart not found")))
                .bodyToMono(CartResponse.class)
                .block();
    }

    public void updateCartItemPrices(String cartId, List<CartItemResponse> priceChanges) {
        webClientBuilder.build()
            .put()
            .uri("http://cart-service/carts/{id}/prices", cartId)
            .bodyValue(priceChanges)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new BadRequestException(ExType.CART_NOT_FOUND, "cart not found")))
            .bodyToMono(CartResponse.class)
            .block();
    }

    public void clearCart(String cartId) {
        webClientBuilder.build()
                .delete()
                .uri("http://cart-service/carts/{id}/items", cartId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new BadRequestException(ExType.CART_NOT_FOUND, "cart not found")))
                .bodyToMono(Void.class)
                .block();
    }
}
