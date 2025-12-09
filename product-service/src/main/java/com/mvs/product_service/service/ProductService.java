package com.mvs.product_service.service;

import com.mvs.common_module.events.KafkaTopics;
import com.mvs.common_module.events.ProductCreatedEvent;
import com.mvs.common_module.exceptions.ExType;
import com.mvs.common_module.exceptions.exs.NotFoundException;
import com.mvs.product_service.dto.*;
import com.mvs.product_service.enums.ProductStatus;
import com.mvs.product_service.mapper.ProductMapper;
import com.mvs.product_service.model.Product;
import com.mvs.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final ProductMapper productMapper;

    public ProductDto createProduct(ProductCreateRequest request) {
        log.info("create product {}", request.getName());
        Product product = productMapper.toModel(request);
        product.setStatus(ProductStatus.ACTIVE);
        Product saved = productRepository.save(product);

        ProductCreatedEvent event = new ProductCreatedEvent(Instant.now(), saved.getId());
        kafkaTemplate.send(KafkaTopics.PRODUCT_CREATED, event);
        return productMapper.toDto(saved);
    }

    public ProductDto getProduct(String id) {
        Product product = getProductById(id);
        return productMapper.toDto(product);
    }

    public Product getProductById(String id) {
        log.info("get product by id = {}", id);
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException(ExType.PRODUCT_NOT_FOUND, "Product not found"));
    }

    public PaginationDto<ProductDto> searchProducts(ProductSearchParams params) {
        log.info("search products params {}", params.toString());
        Page<Product> page = productRepository.searchProducts(params);
        return new PaginationDto<>(productMapper.toDto(page.getContent()), page);
    }

    public ProductDto updateProduct(ProductUpdateRequest request, String id) {
        log.info("update product id = {}", id);
        Product product = this.getProductById(id);
        productMapper.Update(request, product);
        /*product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategoryId(request.getCategoryId());
        product.setStock(request.getStock());
        product.setPrice(request.getPrice());*/
        productRepository.save(product);
        return productMapper.toDto(product);
    }

    public ProductDto deleteProduct(String id) {
        log.info("delete product id = {}", id);
        Product product = this.getProductById(id);
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
        return productMapper.toDto(product);
    }
}
