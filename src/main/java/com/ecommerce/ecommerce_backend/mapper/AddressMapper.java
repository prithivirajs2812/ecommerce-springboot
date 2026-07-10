package com.ecommerce.ecommerce_backend.mapper;

import com.ecommerce.ecommerce_backend.dto.address.AddressRequest;
import com.ecommerce.ecommerce_backend.dto.address.AddressResponse;
import com.ecommerce.ecommerce_backend.entity.Address;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse toResponse(Address address);

    Address toEntity(AddressRequest request);

    void updateEntityFromRequest(AddressRequest request, @MappingTarget Address address);
}