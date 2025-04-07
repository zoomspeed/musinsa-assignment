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

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "category_code", nullable = false)
    private String categoryCode;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private int price;

    @Builder
    public ProductView(Long id, Long productId, String productName, Long brandId, String brandName,
                       Long categoryId, String categoryName, String categoryCode, int price) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
        this.price = price;
    }
} 