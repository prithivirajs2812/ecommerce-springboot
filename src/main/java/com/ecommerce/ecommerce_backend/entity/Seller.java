package com.ecommerce.ecommerce_backend.entity;

import com.ecommerce.ecommerce_backend.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sellers")
public class Seller extends BaseEntity {

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false, unique = true)
    private String gstNumber;

    private String address;

    @Builder.Default
    private boolean verified = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();
}