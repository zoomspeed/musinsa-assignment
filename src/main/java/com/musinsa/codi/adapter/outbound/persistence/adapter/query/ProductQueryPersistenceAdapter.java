package com.musinsa.codi.adapter.outbound.persistence.adapter.query;

import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import com.musinsa.codi.adapter.outbound.persistence.repository.query.ProductQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductQueryPersistenceAdapter implements ProductQueryPort {
    private final ProductQueryRepository productQueryRepository;

    @Override
    public List<ProductView> findAll() {
        return productQueryRepository.findAll();
    }

    @Override
    public Optional<ProductView> findById(Long id) {
        return productQueryRepository.findById(id);
    }

    @Override
    public List<ProductView> findByCategory(Category category) {
        return productQueryRepository.findByCategory(category);
    }

    @Override
    public List<ProductView> findByPriceRange(Category category, int minPrice, int maxPrice) {
        return productQueryRepository.findByPriceRange(category, minPrice, maxPrice);
    }

    @Override
    public List<ProductView> findByBrandId(Long brandId) {
        return productQueryRepository.findByBrandId(brandId);
    }
} 