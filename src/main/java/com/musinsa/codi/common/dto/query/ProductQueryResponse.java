package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.domain.model.query.ProductView;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductQueryResponse {
    private Long id;
    private Long productId;
    private String categoryCode;
    private String categoryName;
    private int price;
    private Long brandId;
    private String brandName;

    @Builder
    public ProductQueryResponse(Long id, Long productId, String categoryCode, String categoryName, int price, Long brandId, String brandName) {
        this.id = id;
        this.productId = productId;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.price = price;
        this.brandId = brandId;
        this.brandName = brandName;
    }

    public static ProductQueryResponse from(ProductView productView) {
        return ProductQueryResponse.builder()
                .id(productView.getId())
                .productId(productView.getProductId())
                .categoryCode(productView.getCategoryCode())
                .categoryName(productView.getCategoryName())
                .price(productView.getPrice())
                .brandId(productView.getBrandId())
                .brandName(productView.getBrandName())
                .build();
    }
} 