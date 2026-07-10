package com.ecommerce.ecommerce_backend.controller;

import com.ecommerce.ecommerce_backend.dto.wishlist.AddWishlistItemRequest;
import com.ecommerce.ecommerce_backend.dto.wishlist.WishlistItemResponse;
import com.ecommerce.ecommerce_backend.security.UserPrincipal;
import com.ecommerce.ecommerce_backend.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> getWishlist(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(wishlistService.getWishlist(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<WishlistItemResponse> addToWishlist(
            @Valid @RequestBody AddWishlistItemRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        WishlistItemResponse response = wishlistService.addToWishlist(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal principal) {
        wishlistService.removeFromWishlist(principal.getId(), productId);
        return ResponseEntity.noContent().build();
    }
}