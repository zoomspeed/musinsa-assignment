package com.musinsa.codi.domain.model.query;

import com.musinsa.codi.domain.model.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brand_view")
@Getter
@NoArgsConstructor
public class BrandView {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "brand_id", updatable = false)
    private List<ProductView> products = new ArrayList<>();

    @Builder
    public BrandView(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addProduct(ProductView product) {
        products.add(product);
    }

    public void removeProduct(ProductView product) {
        products.remove(product);
    }
} 