package com.mvs.product_service.mapper;

import com.mvs.product_service.dto.ProductCreateRequest;
import com.mvs.product_service.dto.ProductDto;
import com.mvs.product_service.dto.ProductUpdateRequest;
import com.mvs.product_service.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);
    Product toModel(ProductCreateRequest request);
    List<ProductDto> toDto(List<Product> products);

    @Mapping(target = "id", ignore = true)
    void Update(ProductUpdateRequest request, @MappingTarget Product product);
}
