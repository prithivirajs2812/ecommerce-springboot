package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.product.ProductCreateRequest;
import com.ecommerce.ecommerce_backend.dto.product.ProductResponse;
import com.ecommerce.ecommerce_backend.dto.product.ProductUpdateRequest;
import com.ecommerce.ecommerce_backend.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerName", source = "seller.businessName")
    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "seller", ignore = true)
    Product toEntity(ProductCreateRequest request);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "seller", ignore = true)
    void updateEntityFromRequest(ProductUpdateRequest request, @MappingTarget Product product);
}