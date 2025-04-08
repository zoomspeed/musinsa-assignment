package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.application.usecase.query.BrandQueryUseCase;
import com.musinsa.codi.common.dto.query.BrandLowestPriceResponse;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.model.query.BrandView;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
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
        List<ProductView> allProducts = productQueryPort.findAll();

        // 브랜드별로 상품들을 그룹화
        Map<String, List<ProductView>> productsByBrand = allProducts.stream()
                .collect(Collectors.groupingBy(ProductView::getBrandName));

        // 각 브랜드의 총 가격을 계산하고 최저가격 브랜드를 찾음
        return productsByBrand.entrySet().stream()
                .map(entry -> BrandLowestPriceResponse.from(entry.getKey(), entry.getValue()))
                .min((a, b) -> Long.compare(a.getTotalPrice(), b.getTotalPrice()))
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
    }
} 