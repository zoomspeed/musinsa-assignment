package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.domain.model.command.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryPriceRangeResponse {
    private String category;
    private List<BrandPrice> lowestPrice;
    private List<BrandPrice> highestPrice;

    @Getter
    @Builder
    public static class BrandPrice {
        private String brandName;
        private long price;
    }

    public static CategoryPriceRangeResponse from(Category category, List<BrandPrice> lowestPrice, List<BrandPrice> highestPrice) {
        return CategoryPriceRangeResponse.builder()
                .category(category.getName())
                .lowestPrice(lowestPrice)
                .highestPrice(highestPrice)
                .build();
    }
} 