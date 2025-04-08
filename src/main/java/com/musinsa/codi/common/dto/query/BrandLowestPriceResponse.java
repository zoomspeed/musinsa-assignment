package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.domain.model.query.ProductView;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BrandLowestPriceResponse {
    private String brandName;
    private List<CategoryPrice> categories;
    private long totalPrice;

    @Getter
    @Builder
    public static class CategoryPrice {
        private String categoryName;
        private long price;
    }

    public static BrandLowestPriceResponse from(String brandName, List<ProductView> products) {
        List<CategoryPrice> categoryPrices = products.stream()
                .map(product -> CategoryPrice.builder()
                        .categoryName(product.getCategoryName())
                        .price(product.getPrice())
                        .build())
                .toList();

        long totalPrice = categoryPrices.stream()
                .mapToLong(CategoryPrice::getPrice)
                .sum();

        return BrandLowestPriceResponse.builder()
                .brandName(brandName)
                .categories(categoryPrices)
                .totalPrice(totalPrice)
                .build();
    }
} 