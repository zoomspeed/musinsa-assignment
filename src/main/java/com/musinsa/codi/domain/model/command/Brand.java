package com.musinsa.codi.domain.model.command;

import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@NoArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @Builder
    public Brand(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void addProduct(Product product) {
        if (products.stream().anyMatch(p -> p.getCategory() == product.getCategory())) {
            throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
        }
        product.setBrand(this);
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setBrand(null);
    }
} 