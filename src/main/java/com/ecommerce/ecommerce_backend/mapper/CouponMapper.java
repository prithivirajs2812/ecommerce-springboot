package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.coupon.CouponRequest;
import com.ecommerce.ecommerce_backend.dto.coupon.CouponResponse;
import com.ecommerce.ecommerce_backend.entity.Coupon;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    CouponResponse toResponse(Coupon coupon);

    @Mapping(target = "active", ignore = true)
    Coupon toEntity(CouponRequest request);

    @Mapping(target = "active", ignore = true)
    void updateEntityFromRequest(CouponRequest request, @MappingTarget Coupon coupon);
}