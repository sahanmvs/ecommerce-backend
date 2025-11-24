package com.mvs.cart_service.model;

import com.mvs.cart_service.dto.ProductResponse;
import com.mvs.cart_service.dto.UpdateCartItemPriceRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Document("carts")
public class Cart {
    private String id;
    private Set<CartItem> items = new LinkedHashSet<>(); // todo: user?
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public CartItem getItem(String productId) {
        return items.stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public CartItem addItem(ProductResponse product) {
        CartItem item = getItem(product.getId());
        if (item != null) {
            item.setQuantity(item.getQuantity() + 1);
        } else {
            item = new CartItem();
            item.setProductId(product.getId());
            item.setQuantity(1);
            item.setUnitPrice(product.getPrice());
            items.add(item);
        }
        return item;
    }

    public void removeItem(String productId) {
        CartItem item = getItem(productId);
        items.remove(item);
    }

    public void clearItems() {
        items.clear();
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateItemPrices(List<UpdateCartItemPriceRequest> request) {
        request.forEach(r -> {
            CartItem item = getItem(r.getProductId());
            if (item != null) {
                item.setUnitPrice(r.getUnitPrice());
            }
        });
    }
}
