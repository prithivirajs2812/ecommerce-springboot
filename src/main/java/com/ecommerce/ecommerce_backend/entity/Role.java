package com.ecommerce.ecommerce_backend.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="roles")
public class Role  extends BaseEntity{

    @Column(nullable = false,unique = true,length = 20)
    private String name;

}
