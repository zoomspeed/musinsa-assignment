package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.common.dto.query.BrandLowestPriceResponse;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
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

    public List<BrandView> getBrandsByCategory(CategoryCommandResponse categoryResponse) {
        return brandQueryPort.findByCategory(categoryResponse.toEntity().getId());
    }

    public List<BrandView> getBrandsByPriceRange(CategoryCommandResponse categoryResponse, int minPrice, int maxPrice) {
        return brandQueryPort.findByPriceRange(categoryResponse.toEntity(), minPrice, maxPrice);
    }

    public BrandLowestPriceResponse findLowestPricesByCategory() {
        List<BrandView> allBrands = brandQueryPort.findAll();

        return null;

    }
} 