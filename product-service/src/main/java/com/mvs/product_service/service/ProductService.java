package com.mvs.product_service.service;

import com.mvs.common_module.events.KafkaTopics;
import com.mvs.common_module.events.ProductCreatedEvent;
import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.NotFoundException;
import com.mvs.product_service.dto.ProductDto;
import com.mvs.product_service.dto.ProductSearchParams;
import com.mvs.product_service.model.Product;
import com.mvs.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public Product createProduct(ProductDto productDto) {
        log.info("create product {}", productDto.getName());
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .category(productDto.getCategory())
                .price(productDto.getPrice())
                .build();
        Product saved = productRepository.save(product);

        ProductCreatedEvent event = new ProductCreatedEvent(Instant.now(), saved.getId());
        kafkaTemplate.send(KafkaTopics.PRODUCT_CREATED, event);
        return saved;
    }

    public Product getProduct(String id) {
        return this.getProductById(id);
    }

    private Product getProductById(String id) {
        log.info("get product by id = {}", id);
        Optional<Product> opProduct = productRepository.findById(id);
        if (opProduct.isEmpty()) throw new NotFoundException(ExType.PRODUCT_NOT_FOUND, "Product not found");
        return opProduct.get();
    }

    public Page<Product> searchProducts(ProductSearchParams params) {
        log.info("search products params {}", params.toString());
        return productRepository.searchProducts(params);
    }

    public Product updateProduct(ProductDto productDto, String id) {
        log.info("update product id = {}", id);
        Product product = this.getProductById(id);
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCategory(productDto.getCategory());
        product.setStock(productDto.getStock());
        product.setPrice(productDto.getPrice());
        return productRepository.save(product);
    }

    public Product deleteProduct(String id) {
        log.info("delete product id = {}", id);
        Product product = this.getProductById(id);
        product.setStatus(Product.ProductStatus.DELETED);
        return productRepository.save(product);
    }
}
