package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dto.wishlist.AddWishlistItemRequest;
import com.ecommerce.ecommerce_backend.dto.wishlist.WishlistItemResponse;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.entity.User;
import com.ecommerce.ecommerce_backend.entity.Wishlist;
import com.ecommerce.ecommerce_backend.exception.BadRequestException;
import com.ecommerce.ecommerce_backend.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_backend.mapper.WishlistMapper;
import com.ecommerce.ecommerce_backend.repository.ProductRepository;
import com.ecommerce.ecommerce_backend.repository.UserRepository;
import com.ecommerce.ecommerce_backend.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishlistMapper wishlistMapper;

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(Long userId) {
        return wishlistRepository.findByUser_Id(userId).stream()
                .map(this::toResponseWithStock)
                .toList();
    }

    @Transactional
    public WishlistItemResponse addToWishlist(Long userId, AddWishlistItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (wishlistRepository.existsByUser_IdAndProduct_Id(userId, product.getId())) {
            throw new BadRequestException("This product is already in your wishlist");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        Wishlist saved = wishlistRepository.save(wishlist);
        return toResponseWithStock(saved);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        if (!wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw new ResourceNotFoundException("This product is not in your wishlist");
        }
        wishlistRepository.deleteByUser_IdAndProduct_Id(userId, productId);
    }

    private WishlistItemResponse toResponseWithStock(Wishlist wishlist) {
        WishlistItemResponse response = wishlistMapper.toResponse(wishlist);
        response.setInStock(wishlist.getProduct().getStock() > 0);
        return response;
    }
}