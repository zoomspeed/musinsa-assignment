package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.BrandView;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandQueryService {
    private final BrandQueryPort brandQueryPort;

    public List<BrandView> getAllBrands() {
        return brandQueryPort.findAll();
    }

    public BrandView getBrandByName(String brandName) {
        return brandQueryPort.findByName(brandName)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
    }

    public List<BrandView> getBrandsByCategory(Category category) {
        return brandQueryPort.findByCategory(category);
    }

    public List<BrandView> getBrandsByPriceRange(Category category, int minPrice, int maxPrice) {
        return brandQueryPort.findByPriceRange(category, minPrice, maxPrice);
    }
} 