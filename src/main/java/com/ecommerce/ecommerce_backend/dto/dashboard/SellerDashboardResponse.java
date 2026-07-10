package com.ecommerce.ecommerce_backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerDashboardResponse {
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long totalUnitsSold;
    private long totalProducts;
    private List<ProductPerformanceItem> topProducts;
}