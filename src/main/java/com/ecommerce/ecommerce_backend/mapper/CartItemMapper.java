package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.cart.CartItemResponse;
import com.ecommerce.ecommerce_backend.entity.CartItem;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "productImage", source = "product.image")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "availableStock", source = "product.stock")
    @Mapping(target = "subtotal", ignore = true)
    CartItemResponse toResponse(CartItem cartItem);
}