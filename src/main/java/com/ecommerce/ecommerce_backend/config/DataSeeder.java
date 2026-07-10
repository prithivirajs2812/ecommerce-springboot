package com.ecommerce.ecommerce_backend.config;

import com.ecommerce.ecommerce_backend.entity.Role;
import com.ecommerce.ecommerce_backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        List<String> defaultRoles = List.of("ROLE_USER", "ROLE_SELLER", "ROLE_ADMIN");

        defaultRoles.forEach(roleName -> {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = Role.builder().name(roleName).build();
                roleRepository.save(role);
            }
        });
    }
}