package com.ecommerce.ecommerce_backend.dto.coupon;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponRequest {

    @NotBlank(message = "Coupon code is required")
    @Size(max = 30)
    private String code;

    @NotNull(message = "Discount percent is required")
    @DecimalMin(value = "0.01", message = "Discount must be greater than 0")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100")
    private BigDecimal discountPercent;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;
}