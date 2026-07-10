package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dto.dashboard.*;
import com.ecommerce.ecommerce_backend.entity.Seller;
import com.ecommerce.ecommerce_backend.exception.ForbiddenException;
import com.ecommerce.ecommerce_backend.repository.*;
import com.ecommerce.ecommerce_backend.repository.projection.AdminPlatformSummary;
import com.ecommerce.ecommerce_backend.repository.projection.ProductPerformance;
import com.ecommerce.ecommerce_backend.repository.projection.SellerSalesSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SellerDashboardResponse getSellerDashboard(Long userId) {
        Seller seller = sellerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ForbiddenException("Only registered sellers can access this dashboard"));

        SellerSalesSummary summary = orderItemRepository.getSellerSalesSummary(seller.getId());
        long totalProducts = productRepository.countBySeller_Id(seller.getId());

        List<ProductPerformanceItem> topProducts = orderItemRepository
                .getProductPerformanceForSeller(seller.getId())
                .stream()
                .map(this::toPerformanceItem)
                .limit(10)
                .toList();

        return SellerDashboardResponse.builder()
                .totalOrders(summary.getTotalOrders())
                .totalRevenue(summary.getTotalRevenue())
                .totalUnitsSold(summary.getTotalUnitsSold())
                .totalProducts(totalProducts)
                .topProducts(topProducts)
                .build();
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getAdminDashboard() {
        AdminPlatformSummary orderStats = orderItemRepository.getPlatformOrderStats();

        return AdminDashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalSellers(sellerRepository.count())
                .totalProducts(productRepository.count())
                .totalOrders(orderStats.getTotalOrders())
                .totalRevenue(orderStats.getTotalRevenue())
                .build();
    }

    private ProductPerformanceItem toPerformanceItem(ProductPerformance p) {
        return ProductPerformanceItem.builder()
                .productId(p.getProductId())
                .productTitle(p.getProductTitle())
                .unitsSold(p.getUnitsSold())
                .revenue(p.getRevenue())
                .build();
    }
}