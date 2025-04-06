package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.BrandView;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService {
    private final BrandQueryPort brandQueryPort;
    private final ProductQueryPort productQueryPort;

    public List<BrandView> getBrandsByCategory(Category category) {
        return brandQueryPort.findByCategory(category);
    }

    public List<ProductView> getProductsByCategory(Category category) {
        return productQueryPort.findByCategory(category);
    }

    public List<BrandView> getBrandsByPriceRange(Category category, int minPrice, int maxPrice) {
        return brandQueryPort.findByPriceRange(category, minPrice, maxPrice);
    }

    public List<ProductView> getProductsByPriceRange(Category category, int minPrice, int maxPrice) {
        return productQueryPort.findByPriceRange(category, minPrice, maxPrice);
    }
} 