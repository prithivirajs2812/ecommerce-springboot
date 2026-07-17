package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.admin.AdminUserResponse;
import com.ecommerce.ecommerce_backend.entity.Role;
import com.ecommerce.ecommerce_backend.entity.User;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {

    @Mapping(target = "roles", expression = "java(mapRoleNames(user))")
    AdminUserResponse toResponse(User user);

    default List<String> mapRoleNames(User user) {
        return user.getRoles().stream().map(Role::getName).toList();
    }
}