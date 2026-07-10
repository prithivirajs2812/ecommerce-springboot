package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.Enum.OrderStatus;
import com.ecommerce.ecommerce_backend.Enum.PaymentStatus;
import com.ecommerce.ecommerce_backend.dto.order.*;
import com.ecommerce.ecommerce_backend.entity.*;
import com.ecommerce.ecommerce_backend.exception.BadRequestException;
import com.ecommerce.ecommerce_backend.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_backend.mapper.OrderMapper;
import com.ecommerce.ecommerce_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CouponService couponService;
    private final CouponRedemptionRepository couponRedemptionRepository;

    @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findByCart_Id(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        addressRepository.findById(request.getAddressId())
                .filter(address -> address.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));

        // After:
        Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            coupon = couponService.validateForCheckout(request.getCouponCode(), userId);
        }

        // Step 1: atomically decrement stock for every item, failing fast if any is insufficient
        for (CartItem item : cartItems) {
            int updated = productRepository.decrementStock(item.getProduct().getId(), item.getQuantity());
            if (updated == 0) {
                throw new BadRequestException(
                        "\"" + item.getProduct().getTitle() + "\" no longer has enough stock. Please update your cart."
                );
            }
        }

        // Step 2: build the order and frozen line-item snapshots
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .coupon(coupon)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            BigDecimal lineTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(lineTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(item.getProduct())
                    .quantity(item.getQuantity())
                    .priceAtPurchase(item.getProduct().getPrice())
                    .build();
            order.getItems().add(orderItem);
        }
        if (coupon != null) {
            BigDecimal discount = total.multiply(coupon.getDiscountPercent()).divide(BigDecimal.valueOf(100));
            total = total.subtract(discount);
        }
        BigDecimal finalTotal = total.setScale(2, RoundingMode.HALF_UP);
        order.setTotalAmount(finalTotal);

        Order savedOrder = orderRepository.save(order);

        Payment payment = Payment.builder()
                .order(savedOrder)
                .method(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .amount(finalTotal)
                .build();
        savedOrder.setPayment(payment);

        // Step 4: clear the cart now that checkout has fully succeeded
        if (coupon != null) {
            try {
                CouponRedemption redemption = CouponRedemption.builder()
                        .coupon(coupon)
                        .user(user)
                        .order(savedOrder)
                        .build();
                couponRedemptionRepository.saveAndFlush(redemption);
            } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                throw new BadRequestException("This coupon has already been used");
            }
        }

        cartItemRepository.deleteAllByCart_Id(cart.getId());


        return buildOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUser_Id(userId, pageable).map(this::buildOrderResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId, boolean isAdmin) {
        Order order = isAdmin
                ? findOrderOrThrow(orderId)
                : orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return buildOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = findOrderOrThrow(orderId);

        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());
        Order saved = orderRepository.save(order);
        return buildOrderResponse(saved);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            case DELIVERED -> next == OrderStatus.RETURNED;
            case CANCELLED, RETURNED -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    "Cannot transition order from " + current + " to " + next
            );
        }
    }

    private Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    private OrderResponse buildOrderResponse(Order order) {
        OrderResponse response = orderMapper.toResponse(order);
        List<OrderItemResponse> items = response.getItems();
        if (items != null) {
            items.forEach(item ->
                    item.setLineTotal(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
            );
        }
        return response;
    }
}