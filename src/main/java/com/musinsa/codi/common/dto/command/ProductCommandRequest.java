package com.musinsa.codi.common.dto.command;

import com.musinsa.codi.domain.model.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCommandRequest {
    private String name;
    private Category category;
    private int price;

    public com.musinsa.codi.domain.model.command.Product toProduct() {
        return com.musinsa.codi.domain.model.command.Product.builder()
                .name(name)
                .category(category)
                .price(price)
                .build();
    }
} 