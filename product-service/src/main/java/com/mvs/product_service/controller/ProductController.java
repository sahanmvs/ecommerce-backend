package com.mvs.product_service.controller;

import com.mvs.product_service.dto.*;
import com.mvs.product_service.model.Product;
import com.mvs.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    public ResponseEntity<PaginationDto<ProductDto>> searchProducts(ProductSearchParams params) { // todo: only active products for customers
        return ResponseEntity.ok(productService.searchProducts(params));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@RequestBody @Valid ProductUpdateRequest request, @PathVariable String id) {
        return ResponseEntity.ok(productService.updateProduct(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }
}
