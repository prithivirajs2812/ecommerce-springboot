package com.ecommerce.ecommerce_backend.dto.wishlist;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddWishlistItemRequest {

    @NotNull(message = "Product id is required")
    private Long productId;
}