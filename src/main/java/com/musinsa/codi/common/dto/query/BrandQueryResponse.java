package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.domain.model.query.BrandView;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BrandQueryResponse {
    private Long id;
    private String name;

    public static BrandQueryResponse from(BrandView brandView) {
        BrandQueryResponse response = new BrandQueryResponse();
        response.id = brandView.getId();
        response.name = brandView.getName();
        return response;
    }
} 