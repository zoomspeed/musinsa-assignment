package com.musinsa.codi.common.dto.command;

import com.musinsa.codi.domain.model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCommandRequest {
    @NotBlank(message = "상품명은 필수 입력 항목입니다.")
    private String name;
    
    @NotNull(message = "카테고리는 필수 입력 항목입니다.")
    private Category category;
    
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    public com.musinsa.codi.domain.model.command.Product toProduct() {
        return com.musinsa.codi.domain.model.command.Product.builder()
                .name(name)
                .category(category)
                .price(price)
                .build();
    }
} 