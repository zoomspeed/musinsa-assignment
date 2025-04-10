package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.application.usecase.query.ProductQueryUseCase;
import com.musinsa.codi.common.dto.query.ProductQueryResponse;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.model.query.ProductView;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, id));
    }

    @Override
    public List<ProductQueryResponse> getProductsByCategory(String categoryCode) {
        Long categoryId = categoryCommandPort.findByCode(categoryCode)
                .map(category -> category.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryCode));
                
        List<ProductView> products = productQueryPort.findByCategory(categoryId);
        
        if (products.isEmpty()) {
            throw new BusinessException(ErrorCode.CATEGORY_NO_PRODUCTS, categoryCode);
        }
        
        return products.stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductQueryResponse> getProductsByPriceRange(String categoryCode, int minPrice, int maxPrice) {
        Long categoryId = categoryCommandPort.findByCode(categoryCode)
                .map(category -> category.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryCode));
                
        List<ProductView> products = productQueryPort.findByPriceRange(categoryId, minPrice, maxPrice);
        
        if (products.isEmpty()) {
            throw new BusinessException(ErrorCode.CATEGORY_NO_PRODUCTS, categoryCode);
        }
        
        return products.stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductQueryResponse> getProductsByBrandId(Long brandId) {
        List<ProductView> products = productQueryPort.findByBrandId(brandId);
        
        if (products.isEmpty()) {
            throw new BusinessException(ErrorCode.BRAND_NO_PRODUCTS, brandId);
        }
        
        return products.stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
    }
} 