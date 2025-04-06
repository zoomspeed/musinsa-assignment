package com.musinsa.codi.domain.port.query;

import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.BrandView;
import java.util.List;
import java.util.Optional;

public interface BrandQueryPort {
    List<BrandView> findAll();
    Optional<BrandView> findById(Long id);
    Optional<BrandView> findByName(String name);
    List<BrandView> findByCategory(Category category);
    List<BrandView> findByPriceRange(Category category, int minPrice, int maxPrice);
    BrandView save(BrandView brandView);
    void delete(Long id);
} 