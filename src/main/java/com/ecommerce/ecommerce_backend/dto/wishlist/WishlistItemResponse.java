package com.ecommerce.ecommerce_backend.dto.wishlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productImage;
    private BigDecimal price;
    private boolean inStock;
}