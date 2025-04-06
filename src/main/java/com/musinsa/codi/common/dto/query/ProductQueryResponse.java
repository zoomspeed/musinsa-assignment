package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.ProductView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductQueryResponse {
    private Long id;
    private String name;
    private Category category;
    private int price;
    private Long brandId;
    private String brandName;

    public static ProductQueryResponse from(ProductView productView) {
        return ProductQueryResponse.builder()
                .id(productView.getId())
                .name(productView.getName())
                .category(productView.getCategory())
                .price(productView.getPrice())
                .brandId(productView.getBrandId())
                .brandName(productView.getBrandName())
                .build();
    }
} 