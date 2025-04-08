package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.application.usecase.query.BrandQueryUseCase;
import com.musinsa.codi.common.dto.query.BrandLowestPriceResponse;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.model.query.BrandView;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import com.musinsa.codi.domain.service.query.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandQueryService implements BrandQueryUseCase {
    private final BrandQueryPort brandQueryPort;
    private final ProductQueryPort productQueryPort;

    @Override
    public List<BrandView> getAllBrands() {
        return brandQueryPort.findAll();
    }

    @Override
    public BrandView getBrandByName(String brandName) {
        return brandQueryPort.findByName(brandName)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND, brandName));
    }

    @Override
    public BrandLowestPriceResponse findLowestPricesByCategory() {
        Map<String, List<ProductView>> productsByBrand = groupProductsByBrand();
        return findBrandWithLowestTotalPrice(productsByBrand);
    }

    private Map<String, List<ProductView>> groupProductsByBrand() {
        List<ProductView> allProducts = productQueryPort.findAll();
        return allProducts.stream()
                .collect(Collectors.groupingBy(ProductView::getBrandName));
    }

    private BrandLowestPriceResponse findBrandWithLowestTotalPrice(Map<String, List<ProductView>> productsByBrand) {
        return productsByBrand.entrySet().stream()
                .map(entry -> createBrandLowestPriceResponse(entry.getKey(), entry.getValue()))
                .min((a, b) -> Long.compare(a.getTotalPrice(), b.getTotalPrice()))
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
    }

    private BrandLowestPriceResponse createBrandLowestPriceResponse(String brandName, List<ProductView> products) {
        return BrandLowestPriceResponse.builder()
                .brandName(brandName)
                .categories(createCategoryPrices(products))
                .totalPrice(PriceCalculator.calculateTotalPrice(products))
                .build();
    }

    private List<BrandLowestPriceResponse.CategoryPrice> createCategoryPrices(List<ProductView> products) {
        return products.stream()
                .map(product -> BrandLowestPriceResponse.CategoryPrice.builder()
                        .categoryName(product.getCategoryCode())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());
    }
} 