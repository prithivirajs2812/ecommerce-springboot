package com.ecommerce.ecommerce_backend.repository;

import com.ecommerce.ecommerce_backend.entity.OrderItem;
import com.ecommerce.ecommerce_backend.repository.projection.AdminPlatformSummary;
import com.ecommerce.ecommerce_backend.repository.projection.ProductPerformance;
import com.ecommerce.ecommerce_backend.repository.projection.SellerSalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_Id(Long orderId);

    boolean existsByOrder_User_IdAndProduct_Id(Long userId, Long productId);

    @Query("""
        SELECT
            COUNT(DISTINCT oi.order.id) AS totalOrders,
            COALESCE(SUM(oi.priceAtPurchase * oi.quantity), 0) AS totalRevenue,
            COALESCE(SUM(oi.quantity), 0) AS totalUnitsSold
        FROM OrderItem oi
        WHERE oi.product.seller.id = :sellerId
        """)
    SellerSalesSummary getSellerSalesSummary(@Param("sellerId") Long sellerId);

    @Query("""
        SELECT
            oi.product.id AS productId,
            oi.product.title AS productTitle,
            SUM(oi.quantity) AS unitsSold,
            SUM(oi.priceAtPurchase * oi.quantity) AS revenue
        FROM OrderItem oi
        WHERE oi.product.seller.id = :sellerId
        GROUP BY oi.product.id, oi.product.title
        ORDER BY revenue DESC
        """)
    List<ProductPerformance> getProductPerformanceForSeller(@Param("sellerId") Long sellerId);

    @Query("""
    SELECT
        COUNT(DISTINCT oi.order.id) AS totalOrders,
        COALESCE(SUM(oi.priceAtPurchase * oi.quantity), 0) AS totalRevenue
    FROM OrderItem oi
    """)
    AdminPlatformSummary getPlatformOrderStats();
}