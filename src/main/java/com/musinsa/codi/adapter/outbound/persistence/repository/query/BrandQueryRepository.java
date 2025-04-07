package com.musinsa.codi.adapter.outbound.persistence.repository.query;

import com.musinsa.codi.domain.model.command.Category;
import com.musinsa.codi.domain.model.query.BrandView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandQueryRepository extends JpaRepository<BrandView, Long> {
    Optional<BrandView> findByName(String name);
    
    @Query("SELECT DISTINCT b FROM BrandView b JOIN b.products p WHERE p.categoryId = :categoryId")
    List<BrandView> findByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT DISTINCT b FROM BrandView b JOIN b.products p WHERE p.categoryId = :category AND p.price BETWEEN :minPrice AND :maxPrice")
    List<BrandView> findByCategoryAndPriceBetween(@Param("category") Category category, @Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice);
} 