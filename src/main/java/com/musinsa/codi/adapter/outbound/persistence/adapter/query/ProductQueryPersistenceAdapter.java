package com.musinsa.codi.adapter.outbound.persistence.adapter.query;

import com.musinsa.codi.domain.model.command.Category;
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
    public List<ProductView> findByCategory(Long categoryId) {
        return productQueryRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<ProductView> findByPriceRange(Long categoryId, int minPrice, int maxPrice) {
        return productQueryRepository.findByPriceRange(categoryId, minPrice, maxPrice);
    }

    @Override
    public List<ProductView> findByBrandId(Long brandId) {
        return productQueryRepository.findByBrandId(brandId);
    }

    @Override
    public ProductView save(ProductView productView) {
        return productQueryRepository.save(productView);
    }

    @Override
    public void delete(Long id) {
        productQueryRepository.findById(id).ifPresent(productQueryRepository::delete);
    }
    
    @Override
    public List<ProductView> findByProductId(Long productId) {
        return productQueryRepository.findById(productId)
            .map(List::of)
            .orElse(List.of());
    }
    
    @Override
    public Optional<ProductView> findByProductIdAndCategoryId(Long productId, Long categoryId) {
        return productQueryRepository.findByIdAndCategoryId(productId, categoryId);
    }

    @Override
    public void deleteById(Long id) {
        productQueryRepository.deleteById(id);
    }
} 