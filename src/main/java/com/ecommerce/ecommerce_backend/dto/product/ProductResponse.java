package com.ecommerce.ecommerce_backend.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Long categoryId;
    private String categoryName;
    private Long sellerId;
    private String sellerName;
    private LocalDateTime createdAt;
}