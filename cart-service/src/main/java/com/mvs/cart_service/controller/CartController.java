package com.mvs.cart_service.controller;

import com.mvs.cart_service.dto.*;
import com.mvs.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart() {
        return ResponseEntity.ok(cartService.createCart());
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable String cartId,
            @RequestBody @Valid AddItemToCartRequest request)
    {
        return ResponseEntity.ok(cartService.addToCart(cartId, request));
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable String cartId) {
        return ResponseEntity.ok(cartService.getCart(cartId));
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartItemDto> updateCartItem(
            @Valid @RequestBody UpdateCartItemRequest request,
            @PathVariable String cartId, @PathVariable String productId
    ) {
        return ResponseEntity.ok(cartService.updateCartItem(cartId, productId, request));
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable String cartId, @PathVariable String productId) {
        cartService.removeCartItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(@PathVariable String cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{cartId}/prices")
    public ResponseEntity<CartDto> updateCartPrices(@RequestBody List<UpdateCartItemPriceRequest> request, @PathVariable String cartId) {
        return ResponseEntity.ok(cartService.updateCartPrices(cartId, request));
    }
}
