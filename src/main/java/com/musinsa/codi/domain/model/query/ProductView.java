package com.musinsa.codi.domain.model.query;

import com.musinsa.codi.domain.model.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_views")
@Getter
@NoArgsConstructor
public class ProductView {
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
    private BrandView brand;

    @Builder
    public ProductView(Long id, String name, Category category, int price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public void setBrand(BrandView brand) {
        this.brand = brand;
    }
} 