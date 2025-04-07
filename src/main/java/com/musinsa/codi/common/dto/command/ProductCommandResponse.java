package com.musinsa.codi.common.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductCommandResponse {
    private boolean success;
    private String message;
    private Long productId;
    private Long brandId;
} 