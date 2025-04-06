package com.musinsa.codi.domain.model.command;

import com.musinsa.codi.domain.model.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Builder
    public Product(Long id, String name, Category category, int price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void updatePrice(int price) {
        this.price = price;
    }
} 