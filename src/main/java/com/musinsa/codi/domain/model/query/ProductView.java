package com.musinsa.codi.domain.model.query;

import com.musinsa.codi.domain.model.command.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_view")
public class ProductView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private int price;

    @Builder
    public ProductView(Long id, Long productId, Long brandId, String brandName, Category category, int price) {
        this.id = id;
        this.productId = productId;
        this.brandId = brandId;
        this.brandName = brandName;
        this.category = category;
        this.price = price;
    }
} 