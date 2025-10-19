package com.mvs.product_service.service;

import com.mvs.product_service.dto.ProductDto;
import com.mvs.product_service.model.Product;
import com.mvs.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductDto productDto) {
        log.info("create product {}", productDto.getName());
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .category(productDto.getCategory())
                .stock(productDto.getStock())
                .price(productDto.getPrice())
                .build();
        return productRepository.save(product);
    }

    public Product getProduct(String id) {
        return this.getProductById(id);
    }

    private Product getProductById(String id) {
        log.info("get product by id = {}", id);
        Optional<Product> opProduct = productRepository.findById(id);
        if (opProduct.isEmpty()) throw new RuntimeException("Product not found");
        return opProduct.get();
    }
}
