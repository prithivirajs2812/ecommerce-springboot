package com.ecommerce.ecommerce_backend.repository.projection;

import java.math.BigDecimal;

public interface SellerSalesSummary {
    Long getTotalOrders();
    BigDecimal getTotalRevenue();
    Long getTotalUnitsSold();
}