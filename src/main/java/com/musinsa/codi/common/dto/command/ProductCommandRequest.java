package com.musinsa.codi.common.dto.command;

import com.musinsa.codi.domain.model.command.Category;
import com.musinsa.codi.domain.model.command.Product;
import com.musinsa.codi.domain.port.command.CategoryCommandPort;
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
    
    @NotBlank(message = "카테고리 코드는 필수 입력 항목입니다.")
    private String categoryCode;
    
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    public Product toProduct(CategoryCommandPort categoryCommandPort) {
        Category category = categoryCommandPort.findByCode(categoryCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode));
        
        return Product.builder()
                .name(name)
                .category(category)
                .price(price)
                .build();
    }
} 