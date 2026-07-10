package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.category.CategoryRequest;
import com.ecommerce.ecommerce_backend.dto.category.CategoryResponse;
import com.ecommerce.ecommerce_backend.entity.Category;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "productCount", ignore = true)
    CategoryResponse toResponse(Category category);

    Category toEntity(CategoryRequest request);

    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}