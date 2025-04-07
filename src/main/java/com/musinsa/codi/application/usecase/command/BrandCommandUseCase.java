package com.musinsa.codi.application.usecase.command;

import com.musinsa.codi.common.dto.command.BrandCommandRequest;
import com.musinsa.codi.domain.model.command.Brand;

public interface BrandCommandUseCase {
    Brand createBrand(BrandCommandRequest request);
    Brand updateBrand(String brandName, BrandCommandRequest request);
    Brand deleteBrand(String brandName);
} 