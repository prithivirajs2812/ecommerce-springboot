package com.ecommerce.ecommerce_backend.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRatingSummary {
    private Long productId;
    private Double averageRating;
    private long totalReviews;
}