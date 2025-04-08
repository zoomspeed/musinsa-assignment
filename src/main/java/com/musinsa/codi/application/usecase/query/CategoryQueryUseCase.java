package com.musinsa.codi.application.usecase.query;

import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.common.dto.query.CategoryLowestPriceResponse;
import com.musinsa.codi.common.dto.query.CategoryPriceRangeResponse;
import com.musinsa.codi.common.dto.query.CategoryResponse;

import java.util.List;

public interface CategoryQueryUseCase {
    List<CategoryResponse> getAllCategories();
    CategoryCommandResponse getCategoryByCode(String categoryCode);
    CategoryLowestPriceResponse findLowestPricesByCategory();
    CategoryPriceRangeResponse getCategoryPriceRangeInfo(String categoryCode);
} 