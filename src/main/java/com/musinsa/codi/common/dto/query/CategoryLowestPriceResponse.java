package com.musinsa.codi.common.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryLowestPriceResponse {
    private List<CategoryPrice> categories;
    private int totalPrice;

    @Getter
    @Builder
    public static class CategoryPrice {
        private String category;
        private String brand;
        private int price;
    }
} 