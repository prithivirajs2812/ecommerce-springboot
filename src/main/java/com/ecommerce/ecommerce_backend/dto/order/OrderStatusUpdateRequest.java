package com.ecommerce.ecommerce_backend.dto.order;

import com.ecommerce.ecommerce_backend.Enum.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}