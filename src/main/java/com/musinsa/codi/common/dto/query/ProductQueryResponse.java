package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.ProductView;
import lombok.Getter;

@Getter
public class ProductQueryResponse {
    private Long id;
    private String name;
    private Category category;
    private int price;

    public static ProductQueryResponse from(ProductView productView) {
        ProductQueryResponse response = new ProductQueryResponse();
        response.id = productView.getId();
        response.name = productView.getName();
        response.category = productView.getCategory();
        response.price = productView.getPrice();
        return response;
    }
} 