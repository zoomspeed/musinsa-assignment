package com.musinsa.codi.domain.model.query;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brand_views")
@Getter
@NoArgsConstructor
public class BrandView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductView> products = new ArrayList<>();

    @Builder
    public BrandView(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addProduct(ProductView product) {
        products.add(product);
        product.setBrand(this);
    }
} 