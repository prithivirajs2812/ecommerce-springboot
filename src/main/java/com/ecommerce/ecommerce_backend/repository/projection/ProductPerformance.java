package com.ecommerce.ecommerce_backend.repository.projection;

import java.math.BigDecimal;

public interface ProductPerformance {
    Long getProductId();
    String getProductTitle();
    Long getUnitsSold();
    BigDecimal getRevenue();
}