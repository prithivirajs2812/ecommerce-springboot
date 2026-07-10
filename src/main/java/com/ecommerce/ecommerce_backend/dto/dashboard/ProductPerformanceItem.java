package com.ecommerce.ecommerce_backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPerformanceItem {
    private Long productId;
    private String productTitle;
    private Long unitsSold;
    private BigDecimal revenue;
}