package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dto.review.*;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.entity.Review;
import com.ecommerce.ecommerce_backend.entity.User;
import com.ecommerce.ecommerce_backend.exception.BadRequestException;
import com.ecommerce.ecommerce_backend.exception.ForbiddenException;
import com.ecommerce.ecommerce_backend.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_backend.mapper.ReviewMapper;
import com.ecommerce.ecommerce_backend.repository.OrderItemRepository;
import com.ecommerce.ecommerce_backend.repository.ProductRepository;
import com.ecommerce.ecommerce_backend.repository.ReviewRepository;
import com.ecommerce.ecommerce_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForProduct(Long productId, Pageable pageable) {
        return reviewRepository.findByProduct_Id(productId, pageable).map(reviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductRatingSummary getRatingSummary(Long productId) {
        Double average = reviewRepository.findAverageRatingByProductId(productId);
        long total = reviewRepository.findByProduct_Id(productId, Pageable.unpaged()).getTotalElements();

        return ProductRatingSummary.builder()
                .productId(productId)
                .averageRating(average != null ? Math.round(average * 10.0) / 10.0 : null)
                .totalReviews(total)
                .build();
    }

    @Transactional
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (reviewRepository.existsByProduct_IdAndUser_Id(request.getProductId(), userId)) {
            throw new BadRequestException("You have already reviewed this product");
        }

        if (!orderItemRepository.existsByOrder_User_IdAndProduct_Id(userId, request.getProductId())) {
            throw new ForbiddenException("You can only review products you have purchased");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = reviewMapper.toEntity(request);
        review.setProduct(product);
        review.setUser(user);

        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponse(saved);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, Long userId, ReviewRequest request) {
        Review review = reviewRepository.findByIdAndUser_Id(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        reviewMapper.updateEntityFromRequest(request, review);
        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponse(saved);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!isAdmin && !review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to delete this review");
        }

        reviewRepository.delete(review);
    }
}