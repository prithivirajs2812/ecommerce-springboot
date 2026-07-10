package com.ecommerce.ecommerce_backend.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotNull(message = "Shipping address id is required")
    private Long addressId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String couponCode;
}