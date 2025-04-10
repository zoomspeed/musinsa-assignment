package com.musinsa.codi.adapter.outbound.persistence.adapter.query;

import com.musinsa.codi.domain.model.command.Category;
import com.musinsa.codi.domain.model.query.BrandView;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import com.musinsa.codi.adapter.outbound.persistence.repository.query.BrandQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BrandQueryPersistenceAdapter implements BrandQueryPort {
    private final BrandQueryRepository brandViewRepository;

    @Override
    public List<BrandView> findAll() {
        return brandViewRepository.findAll();
    }

    @Override
    public Optional<BrandView> findById(Long id) {
        return brandViewRepository.findById(id);
    }

    @Override
    public Optional<BrandView> findByName(String name) {
        return brandViewRepository.findByName(name);
    }

    @Override
    public List<BrandView> findByCategory(Long categoryId) {
        return brandViewRepository.findByCategory(categoryId);
    }

    @Override
    public List<BrandView> findByPriceRange(Category category, int minPrice, int maxPrice) {
        return brandViewRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice);
    }

    @Override
    public BrandView save(BrandView brandView) {
        return brandViewRepository.save(brandView);
    }

    @Override
    public void delete(Long id) {
        brandViewRepository.deleteById(id);
    }
} 