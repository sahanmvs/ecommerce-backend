package com.mvs.product_service.repository;

import com.mvs.product_service.dto.ProductSearchParams;
import com.mvs.product_service.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String>, CustomProductRepository {

}

interface CustomProductRepository {
    Page<Product> searchProducts(ProductSearchParams params);
}

@RequiredArgsConstructor
class CustomProductRepositoryImpl implements CustomProductRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> searchProducts(ProductSearchParams params) {
        Query query = new Query();
        query.addCriteria(params.getProductSearchCriteria());

        Sort sort = Sort.by(params.getSortDirection(), params.getSortBy());
        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);
        query.with(pageable);

        List<Product> products = mongoTemplate.find(query, Product.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Product.class);

        return new PageImpl<>(products, pageable, total);
    }
}
