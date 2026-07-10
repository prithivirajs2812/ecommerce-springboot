package com.ecommerce.ecommerce_backend.repository.projection;

public interface AdminPlatformSummary {
    Long getTotalUsers();
    Long getTotalSellers();
    Long getTotalProducts();
    Long getTotalOrders();
    java.math.BigDecimal getTotalRevenue();
}