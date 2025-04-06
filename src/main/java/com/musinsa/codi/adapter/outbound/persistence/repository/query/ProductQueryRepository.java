package com.musinsa.codi.adapter.outbound.persistence.repository.query;

import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductQueryRepository extends JpaRepository<ProductView, Long> {
    List<ProductView> findByCategory(Category category);
    List<ProductView> findByCategoryAndPriceBetween(Category category, int minPrice, int maxPrice);
} 