package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.review.ReviewRequest;
import com.ecommerce.ecommerce_backend.dto.review.ReviewResponse;
import com.ecommerce.ecommerce_backend.entity.Review;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "reviewerName", expression = "java(review.getUser().getFirstName() + \" \" + review.getUser().getLastName())")
    ReviewResponse toResponse(Review review);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    Review toEntity(ReviewRequest request);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "comment", source = "comment")
    void updateEntityFromRequest(ReviewRequest request, @MappingTarget Review review);
}