package com.musinsa.codi.application.usecase.query;

import com.musinsa.codi.common.dto.query.ProductQueryResponse;

import java.util.List;

public interface ProductQueryUseCase {
    List<ProductQueryResponse> getAllProducts();
    ProductQueryResponse getProductById(Long id);
    List<ProductQueryResponse> getProductsByCategory(String categoryCode);
    List<ProductQueryResponse> getProductsByPriceRange(String categoryCode, int minPrice, int maxPrice);
    List<ProductQueryResponse> getProductsByBrandId(Long brandId);
} 