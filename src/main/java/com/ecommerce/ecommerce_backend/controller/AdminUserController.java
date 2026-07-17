package com.ecommerce.ecommerce_backend.controller;

import com.ecommerce.ecommerce_backend.dto.admin.AdminSellerResponse;
import com.ecommerce.ecommerce_backend.dto.admin.AdminUserResponse;
import com.ecommerce.ecommerce_backend.security.UserPrincipal;
import com.ecommerce.ecommerce_backend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getAllUsers(pageable));
    }

    @PatchMapping("/users/{id}/ban")
    public ResponseEntity<AdminUserResponse> banUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(adminUserService.banUser(id, principal.getId()));
    }

    @PatchMapping("/users/{id}/unban")
    public ResponseEntity<AdminUserResponse> unbanUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.unbanUser(id));
    }

    @GetMapping("/sellers")
    public ResponseEntity<Page<AdminSellerResponse>> getAllSellers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getAllSellers(pageable));
    }

    @GetMapping("/sellers/pending")
    public ResponseEntity<Page<AdminSellerResponse>> getPendingSellers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getPendingSellers(pageable));
    }

    @PatchMapping("/sellers/{id}/verify")
    public ResponseEntity<AdminSellerResponse> verifySeller(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.verifySeller(id));
    }
}