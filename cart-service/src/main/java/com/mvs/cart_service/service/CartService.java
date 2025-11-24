package com.mvs.cart_service.service;

import com.mvs.cart_service.client.ProductClient;
import com.mvs.cart_service.dto.*;
import com.mvs.cart_service.mapper.CartMapper;
import com.mvs.cart_service.model.Cart;
import com.mvs.cart_service.model.CartItem;
import com.mvs.cart_service.repository.CartRepository;
import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.BadRequestException;
import com.mvs.common_module.exceptions.exs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductClient productClient;

    public CartDto createCart() {
        log.info("Creating new cart");
        Cart cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(String cartId, AddItemToCartRequest request) {
        log.info("Adding item to cart {} product {}", cartId, request.getProductId());
        Cart cart = getCartById(cartId);
        ProductResponse product = productClient.getProduct(request.getProductId());
        CartItem item = cart.addItem(product);
        cartRepository.save(cart);
        return cartMapper.toDto(item);
    }

    public CartDto getCart(String cartId) {
        log.info("Retrieving cart {}", cartId);
        Cart cart = getCartById(cartId);
        return cartMapper.toDto(cart);
    }

    public CartItemDto updateCartItem(String cartId, String productId, UpdateCartItemRequest request) {
        log.info("Updating item in cart {} product {} quantity {}", cartId, productId, request.getQuantity());
        Cart cart = getCartById(cartId);
        CartItem item = cart.getItem(productId);
        if (item == null) throw new BadRequestException(ExType.PRODUCT_NOT_FOUND, "product not found");
        item.setQuantity(request.getQuantity());
        cartRepository.save(cart);
        return cartMapper.toDto(item);
    }

    public void removeCartItem(String cartId, String productId) {
        log.info("Removing item from cart {} product {}", cartId, productId);
        Cart cart = getCartById(cartId);
        CartItem item = cart.getItem(productId);
        if (item == null) throw new BadRequestException(ExType.PRODUCT_NOT_FOUND, "product not found");
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    public void clearCart(String cartId) {
        log.info("Clearing cart {}", cartId);
        Cart cart = getCartById(cartId);
        cart.clearItems();
        cartRepository.save(cart);
    }

    public CartDto updateCartPrices(String cartId, List<UpdateCartItemPriceRequest> request) {
        log.info("Updating cart {} prices", cartId);
        Cart cart = getCartById(cartId);
        cart.updateItemPrices(request);
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    public Cart getCartById(String cartId) {
        log.info("get cart by cartId {}", cartId);
        return cartRepository.findById(cartId).orElseThrow(() -> new NotFoundException(ExType.CART_NOT_FOUND, "cart not found"));
    }
}
