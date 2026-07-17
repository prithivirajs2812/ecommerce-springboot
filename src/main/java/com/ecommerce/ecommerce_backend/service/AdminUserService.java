package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dto.admin.AdminSellerResponse;
import com.ecommerce.ecommerce_backend.dto.admin.AdminUserResponse;
import com.ecommerce.ecommerce_backend.entity.Seller;
import com.ecommerce.ecommerce_backend.entity.User;
import com.ecommerce.ecommerce_backend.exception.BadRequestException;
import com.ecommerce.ecommerce_backend.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_backend.mapper.AdminSellerMapper;
import com.ecommerce.ecommerce_backend.mapper.AdminUserMapper;
import com.ecommerce.ecommerce_backend.repository.RefreshTokenRepository;
import com.ecommerce.ecommerce_backend.repository.SellerRepository;
import com.ecommerce.ecommerce_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AdminUserMapper adminUserMapper;
    private final AdminSellerMapper adminSellerMapper;

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(adminUserMapper::toResponse);
    }

    @Transactional
    public AdminUserResponse banUser(Long targetUserId, Long requestingAdminId) {
        if (targetUserId.equals(requestingAdminId)) {
            throw new BadRequestException("You cannot ban your own account");
        }

        User target = findUserOrThrow(targetUserId);

        boolean targetIsAdmin = target.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (targetIsAdmin) {
            throw new BadRequestException("Cannot ban another admin account through this endpoint");
        }

        target.setEnabled(false);
        User saved = userRepository.save(target);

        refreshTokenRepository.deleteByUser_Id(targetUserId);

        return adminUserMapper.toResponse(saved);
    }

    @Transactional
    public AdminUserResponse unbanUser(Long targetUserId) {
        User target = findUserOrThrow(targetUserId);
        target.setEnabled(true);
        User saved = userRepository.save(target);
        return adminUserMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<AdminSellerResponse> getAllSellers(Pageable pageable) {
        return sellerRepository.findAll(pageable).map(adminSellerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AdminSellerResponse> getPendingSellers(Pageable pageable) {
        return sellerRepository.findByVerifiedFalse(pageable).map(adminSellerMapper::toResponse);
    }

    @Transactional
    public AdminSellerResponse verifySeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + sellerId));

        seller.setVerified(true);
        Seller saved = sellerRepository.save(seller);
        return adminSellerMapper.toResponse(saved);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}