package com.ecommerce.ecommerce_backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSellerResponse {
    private Long id;
    private String businessName;
    private String gstNumber;
    private boolean verified;
    private String ownerEmail;
}