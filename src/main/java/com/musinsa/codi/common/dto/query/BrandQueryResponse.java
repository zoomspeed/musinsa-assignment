package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.domain.model.query.BrandView;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BrandQueryResponse {
    private Long id;
    private String name;
    private List<ProductQueryResponse> products;

    public static BrandQueryResponse from(BrandView brandView) {
        BrandQueryResponse response = new BrandQueryResponse();
        response.id = brandView.getId();
        response.name = brandView.getName();
        response.products = brandView.getProducts().stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
        return response;
    }
} 