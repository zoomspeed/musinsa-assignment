package com.musinsa.codi.common.dto.command;

import com.musinsa.codi.domain.model.command.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCommandResponse {
    private Long categoryId;
    private String code;
    private String name;
    private int displayOrder;
    private String description;
    private boolean success;
    private String message;

    public static CategoryCommandResponse from(Category category) {
        return CategoryCommandResponse.builder()
                .categoryId(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .displayOrder(category.getDisplayOrder())
                .description(category.getDescription())
                .success(true)
                .message("카테고리가 성공적으로 처리되었습니다.")
                .build();
    }

    public static CategoryCommandResponse error(String message) {
        return CategoryCommandResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public Category toEntity() {
        return Category.builder()
                .code(code)
                .name(name)
                .displayOrder(displayOrder)
                .description(description)
                .build();
    }
} 