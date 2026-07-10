package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.order.OrderItemResponse;
import com.ecommerce.ecommerce_backend.dto.order.OrderResponse;
import com.ecommerce.ecommerce_backend.entity.Order;
import com.ecommerce.ecommerce_backend.entity.OrderItem;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "paymentMethod", source = "payment.method")
    @Mapping(target = "paymentStatus", source = "payment.status")
    OrderResponse toResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "productImage", source = "product.image")
    @Mapping(target = "lineTotal", ignore = true)
    OrderItemResponse toItemResponse(OrderItem item);
}