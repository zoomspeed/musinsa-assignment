package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.application.usecase.query.ProductQueryUseCase;
import com.musinsa.codi.common.dto.query.ProductQueryResponse;
import com.musinsa.codi.domain.port.command.CategoryCommandPort;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService implements ProductQueryUseCase {
    private final ProductQueryPort productQueryPort;
    private final CategoryCommandPort categoryCommandPort;

    @Override
    public List<ProductQueryResponse> getAllProducts() {
        return productQueryPort.findAll().stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public ProductQueryResponse getProductById(Long id) {
        return productQueryPort.findById(id)
                .map(ProductQueryResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다: " + id));
    }

    @Override
    public List<ProductQueryResponse> getProductsByCategory(String categoryCode) {
        Long categoryId = categoryCommandPort.findByCode(categoryCode)
                .map(category -> category.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode));
        return productQueryPort.findByCategory(categoryId).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductQueryResponse> getProductsByPriceRange(String categoryCode, int minPrice, int maxPrice) {
        Long categoryId = categoryCommandPort.findByCode(categoryCode)
                .map(category -> category.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode));
        return productQueryPort.findByPriceRange(categoryId, minPrice, maxPrice).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductQueryResponse> getProductsByBrandId(Long brandId) {
        return productQueryPort.findByBrandId(brandId).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
    }
} 