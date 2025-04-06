package com.musinsa.codi.domain.model.query;

import com.musinsa.codi.domain.model.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_view", uniqueConstraints = {
    // @UniqueConstraint(columnNames = {"id", "category"})
})
@Getter
@Setter
@NoArgsConstructor
public class ProductView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewId;
    
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private int price;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Builder
    public ProductView(Long id, String name, Category category, int price, Long brandId, String brandName) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.brandId = brandId;
        this.brandName = brandName;
    }
} 