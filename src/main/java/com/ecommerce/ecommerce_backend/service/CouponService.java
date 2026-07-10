package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dto.coupon.CouponRequest;
import com.ecommerce.ecommerce_backend.dto.coupon.CouponResponse;
import com.ecommerce.ecommerce_backend.entity.Coupon;
import com.ecommerce.ecommerce_backend.exception.BadRequestException;
import com.ecommerce.ecommerce_backend.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_backend.mapper.CouponMapper;
import com.ecommerce.ecommerce_backend.repository.CouponRedemptionRepository;
import com.ecommerce.ecommerce_backend.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final CouponMapper couponMapper;

    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream().map(couponMapper::toResponse).toList();
    }

    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        if (couponRepository.findByCodeAndActiveTrue(request.getCode()).isPresent()) {
            throw new BadRequestException("An active coupon with this code already exists");
        }

        Coupon coupon = couponMapper.toEntity(request);
        coupon.setActive(true);

        Coupon saved = couponRepository.save(coupon);
        return couponMapper.toResponse(saved);
    }

    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = findCouponOrThrow(id);
        couponMapper.updateEntityFromRequest(request, coupon);
        Coupon saved = couponRepository.save(coupon);
        return couponMapper.toResponse(saved);
    }

    @Transactional
    public CouponResponse deactivateCoupon(Long id) {
        Coupon coupon = findCouponOrThrow(id);
        coupon.setActive(false);
        Coupon saved = couponRepository.save(coupon);
        return couponMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Coupon validateForCheckout(String code, Long userId) {
        Coupon coupon = couponRepository.findByCodeAndActiveTrue(code)
                .orElseThrow(() -> new BadRequestException("Invalid or inactive coupon code"));

        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This coupon has expired");
        }

        if (couponRedemptionRepository.existsByCoupon_IdAndUser_Id(coupon.getId(), userId)) {
            throw new BadRequestException("You have already used this coupon");
        }

        return coupon;
    }

    private Coupon findCouponOrThrow(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
    }
}