package com.musinsa.codi.application.usecase.query;

import com.musinsa.codi.common.dto.query.BrandLowestPriceResponse;
import com.musinsa.codi.domain.model.query.BrandView;

import java.util.List;

public interface BrandQueryUseCase {
    List<BrandView> getAllBrands();
    BrandView getBrandByName(String brandName);
    BrandLowestPriceResponse findLowestPricesByCategory();
}