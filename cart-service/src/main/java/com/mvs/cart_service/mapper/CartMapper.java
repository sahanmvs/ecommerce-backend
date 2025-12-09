package com.mvs.cart_service.mapper;

import com.mvs.cart_service.dto.CartDto;
import com.mvs.cart_service.dto.CartItemDto;
import com.mvs.cart_service.model.Cart;
import com.mvs.cart_service.model.CartItem;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Configuration;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);
    CartItemDto toDto(CartItem cartItem);
}
