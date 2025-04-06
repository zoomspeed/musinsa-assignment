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
    private final ProductQueryRepository productViewRepository;

    @Override
    public List<ProductView> findAll() {
        return productViewRepository.findAll();
    }

    @Override
    public Optional<ProductView> findById(Long id) {
        return productViewRepository.findById(id);
    }

    @Override
    public List<ProductView> findByCategory(Category category) {
        return productViewRepository.findByCategory(category);
    }

    @Override
    public List<ProductView> findByPriceRange(Category category, int minPrice, int maxPrice) {
        return productViewRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice);
    }
} 