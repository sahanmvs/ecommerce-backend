package com.mvs.cart_service.repository;

import com.mvs.cart_service.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
}
