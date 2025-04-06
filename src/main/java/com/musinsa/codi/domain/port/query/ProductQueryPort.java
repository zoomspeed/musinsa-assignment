package com.musinsa.codi.domain.port.query;

import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.ProductView;
import java.util.List;
import java.util.Optional;

public interface ProductQueryPort {
    List<ProductView> findAll();
    Optional<ProductView> findById(Long id);
    List<ProductView> findByCategory(Category category);
    List<ProductView> findByPriceRange(Category category, int minPrice, int maxPrice);
    List<ProductView> findByBrandId(Long brandId);
    ProductView save(ProductView productView);
    void delete(Long id);
    
    // 새로 추가된 메서드
    List<ProductView> findByProductId(Long productId);
    Optional<ProductView> findByProductIdAndCategory(Long productId, Category category);
    void deleteByViewId(Long viewId);
} 