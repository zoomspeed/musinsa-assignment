package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.domain.model.query.ProductView;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse {
    private Long id;
    private Long productId;
    private Long brandId;
    private String brandName;
    private CategoryCommandResponse category;
    private int price;

    @Builder
    public ProductResponse(Long id, Long productId, Long brandId, String brandName, CategoryCommandResponse category, int price) {
        this.id = id;
        this.productId = productId;
        this.brandId = brandId;
        this.brandName = brandName;
        this.category = category;
        this.price = price;
    }

    public static ProductResponse from(ProductView productView) {
        return ProductResponse.builder()
                .id(productView.getId())
                .productId(productView.getProductId())
                .brandId(productView.getBrandId())
                .brandName(productView.getBrandName())
                .category(CategoryCommandResponse.builder()
                        .categoryId(productView.getCategoryId())
                        .name(productView.getCategoryName())
                        .code(productView.getCategoryCode()).build())
                .price(productView.getPrice())
                .build();
    }
} 