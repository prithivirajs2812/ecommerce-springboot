package com.ecommerce.ecommerce_backend.security;

import com.ecommerce.ecommerce_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("productSecurity")
@RequiredArgsConstructor
public class ProductSecurity {

    private final ProductRepository productRepository;

    public boolean isOwner(Long productId, Long userId) {
        return productRepository.existsByIdAndSeller_User_Id(productId, userId);
    }
}