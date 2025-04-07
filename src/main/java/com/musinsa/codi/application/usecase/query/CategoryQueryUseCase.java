package com.musinsa.codi.application.usecase.query;

import com.musinsa.codi.common.dto.query.CategoryLowestPriceResponse;

public interface CategoryQueryUseCase {
    CategoryLowestPriceResponse findLowestPricesByCategory();
} 