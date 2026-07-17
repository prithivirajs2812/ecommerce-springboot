package com.ecommerce.ecommerce_backend.repository;

import com.ecommerce.ecommerce_backend.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByUser_Id(Long userId);

    boolean existsByGstNumber(String gstNumber);

    long count(); // also inherited — no addition needed

    Page<Seller> findByVerifiedFalse(Pageable pageable);
}