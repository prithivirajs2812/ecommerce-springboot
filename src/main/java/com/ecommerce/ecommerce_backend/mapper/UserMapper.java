package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.user.UserProfileResponse;
import com.ecommerce.ecommerce_backend.entity.User;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "Seller", expression = "java(user.getSellerProfile() != null)")
    UserProfileResponse toResponse(User user);
}