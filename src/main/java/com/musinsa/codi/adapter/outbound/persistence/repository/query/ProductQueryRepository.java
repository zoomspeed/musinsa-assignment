package com.musinsa.codi.adapter.outbound.persistence.repository.query;

import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductQueryRepository extends JpaRepository<ProductView, Long> {
    List<ProductView> findByCategory(Category category);
    
    @Query("SELECT p FROM ProductView p WHERE p.category = :category AND p.price BETWEEN :minPrice AND :maxPrice")
    List<ProductView> findByPriceRange(
            @Param("category") Category category,
            @Param("minPrice") int minPrice,
            @Param("maxPrice") int maxPrice
    );
    
    List<ProductView> findByBrandId(Long brandId);
} 