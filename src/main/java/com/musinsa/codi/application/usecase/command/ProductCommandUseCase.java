package com.musinsa.codi.application.usecase.command;

import com.musinsa.codi.common.dto.command.ProductCommandRequest;
import com.musinsa.codi.domain.model.command.Brand;

public interface ProductCommandUseCase {
    Brand addProduct(String brandName, ProductCommandRequest request);
    Brand updateProduct(String brandName, Long productId, ProductCommandRequest request);
    Brand deleteProduct(String brandName, Long productId);
} 