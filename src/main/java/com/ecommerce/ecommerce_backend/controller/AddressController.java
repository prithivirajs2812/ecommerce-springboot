package com.ecommerce.ecommerce_backend.controller;

import com.ecommerce.ecommerce_backend.dto.address.AddressRequest;
import com.ecommerce.ecommerce_backend.dto.address.AddressResponse;
import com.ecommerce.ecommerce_backend.security.UserPrincipal;
import com.ecommerce.ecommerce_backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getMyAddresses(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(addressService.getMyAddresses(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        AddressResponse response = addressService.createAddress(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(addressService.updateAddress(principal.getId(), addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal UserPrincipal principal) {
        addressService.deleteAddress(principal.getId(), addressId);
        return ResponseEntity.noContent().build();
    }
}