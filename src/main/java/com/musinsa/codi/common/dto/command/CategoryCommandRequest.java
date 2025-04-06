package com.musinsa.codi.common.dto.command;

import com.musinsa.codi.domain.model.command.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCommandRequest {
    @NotBlank(message = "카테고리 코드는 필수 입력 항목입니다.")
    private String code;

    @NotBlank(message = "카테고리 이름은 필수 입력 항목입니다.")
    private String name;

    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다.")
    private int displayOrder;

    private String description;

    public Category toEntity() {
        return Category.builder()
                .code(code)
                .name(name)
                .displayOrder(displayOrder)
                .description(description)
                .build();
    }
} 