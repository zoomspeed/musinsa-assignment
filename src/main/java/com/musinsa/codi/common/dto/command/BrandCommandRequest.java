package com.musinsa.codi.common.dto.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BrandCommandRequest {
    @NotBlank(message = "브랜드 이름은 필수입니다.")
    private String name;
} 