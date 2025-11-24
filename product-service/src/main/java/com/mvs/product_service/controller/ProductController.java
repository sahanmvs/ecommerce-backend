package com.mvs.product_service.controller;

import com.mvs.product_service.dto.PaginationDto;
import com.mvs.product_service.dto.ProductDto;
import com.mvs.product_service.dto.ProductSearchParams;
import com.mvs.product_service.model.Product;
import com.mvs.product_service.service.ProductService;
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
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(ProductDto.init(productService.createProduct(productDto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(ProductDto.init(productService.getProduct(id))); // todo: only active products
    }

    @GetMapping
    public ResponseEntity<PaginationDto<ProductDto>> searchProducts(ProductSearchParams params) { // todo: only active products for customers
        Page<Product> page = productService.searchProducts(params);
        return ResponseEntity.ok(new PaginationDto<>(ProductDto.init(page.getContent()), page));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto, @PathVariable String id) {
        return ResponseEntity.ok(ProductDto.init(productService.updateProduct(productDto, id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable String id) {
        return ResponseEntity.ok(ProductDto.init(productService.deleteProduct(id)));
    }
}
