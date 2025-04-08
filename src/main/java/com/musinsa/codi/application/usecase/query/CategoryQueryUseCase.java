package com.musinsa.codi.application.usecase.query;

import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.common.dto.query.CategoryLowestPriceResponse;
import com.musinsa.codi.common.dto.query.CategoryPriceRangeResponse;

public interface CategoryQueryUseCase {
    CategoryLowestPriceResponse findLowestPricesByCategory();
    CategoryPriceRangeResponse getCategoryPriceRangeInfo(CategoryCommandResponse categoryCommandResponse);
} 