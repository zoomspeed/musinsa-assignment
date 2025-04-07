package com.musinsa.codi.common.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BrandCommandResponse {
    private boolean success;
    private String message;
    private Long brandId;
} 