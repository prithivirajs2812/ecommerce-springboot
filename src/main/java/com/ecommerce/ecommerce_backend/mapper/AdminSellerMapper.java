package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.admin.AdminSellerResponse;
import com.ecommerce.ecommerce_backend.entity.Seller;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminSellerMapper {

    @Mapping(target = "ownerEmail", source = "user.email")
    AdminSellerResponse toResponse(Seller seller);
}