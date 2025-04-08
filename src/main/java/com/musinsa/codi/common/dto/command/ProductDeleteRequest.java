package com.musinsa.codi.common.dto.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDeleteRequest {
    @NotBlank(message = "브랜드명은 필수 입력 항목입니다.")
    private String brandName;
} 