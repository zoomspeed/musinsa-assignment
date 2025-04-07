package com.musinsa.codi.adapter.outbound.persistence.repository.query;

import com.musinsa.codi.domain.model.command.Category;
import com.musinsa.codi.domain.model.query.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductQueryRepository extends JpaRepository<ProductView, Long> {
    List<ProductView> findByCategoryId(Long categoryId);
    
    @Query("SELECT p FROM ProductView p WHERE p.categoryId = :categoryId AND p.price BETWEEN :minPrice AND :maxPrice")
    List<ProductView> findByPriceRange(@Param("categoryId") Long categoryId, @Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice);
    
    List<ProductView> findByBrandId(Long brandId);
    
    Optional<ProductView> findByIdAndCategoryId(Long id, Long categoryId);
} 