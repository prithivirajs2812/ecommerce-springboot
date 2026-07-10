package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.wishlist.WishlistItemResponse;
import com.ecommerce.ecommerce_backend.entity.Wishlist;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "productImage", source = "product.image")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "inStock", ignore = true)
    WishlistItemResponse toResponse(Wishlist wishlist);
}