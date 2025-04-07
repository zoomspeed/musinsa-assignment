package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.application.usecase.query.CategoryQueryUseCase;
import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.common.dto.query.CategoryLowestPriceResponse;
import com.musinsa.codi.common.dto.query.CategoryPriceRangeResponse;
import com.musinsa.codi.domain.model.command.Category;
import com.musinsa.codi.domain.model.query.BrandView;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.command.CategoryCommandPort;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService implements CategoryQueryUseCase {
    private final BrandQueryPort brandQueryPort;
    private final ProductQueryPort productQueryPort;
    private final CategoryCommandPort categoryCommandPort;

    public List<BrandView> getBrandsByCategory(CategoryCommandResponse categoryResponse) {
        Category category = categoryCommandPort.findByCode(categoryResponse.getCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryResponse.getCode()));
        return brandQueryPort.findByCategory(category.getId());
    }

    public List<ProductView> getProductsByCategory(CategoryCommandResponse categoryResponse) {
        Category category = categoryCommandPort.findByCode(categoryResponse.getCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryResponse.getCode()));
        return productQueryPort.findByCategory(category.getId());
    }

    public List<BrandView> getBrandsByPriceRange(CategoryCommandResponse categoryResponse, int minPrice, int maxPrice) {
        return brandQueryPort.findByPriceRange(categoryResponse.toEntity(), minPrice, maxPrice);
    }

    public List<ProductView> getProductsByPriceRange(CategoryCommandResponse categoryResponse, int minPrice, int maxPrice) {
        return productQueryPort.findByPriceRange(categoryResponse.toEntity().getId(), minPrice, maxPrice);
    }

    @Override
    public CategoryLowestPriceResponse findLowestPricesByCategory() {
        // 모든 상품 뷰 조회
        List<ProductView> allProducts = productQueryPort.findAll();
        
        // 카테고리별로 그룹화하고 최저가 상품 찾기
        Map<Long, Category> categoryMap = categoryCommandPort.findAll().stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

        Map<Category, ProductView> lowestPriceProducts = allProducts.stream()
                .collect(Collectors.groupingBy(
                        pv -> categoryMap.get(pv.getCategoryId()),  // Long -> Category로 변환
                        Collectors.minBy(Comparator.comparingInt(ProductView::getPrice))
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue().get()
                ));
        // 응답 DTO 구성
        List<CategoryLowestPriceResponse.CategoryPrice> categories = new ArrayList<>();
        int totalPrice = 0;

        // 모든 카테고리를 조회하여 순회
        List<Category> allCategories = categoryCommandPort.findAll();
        for (Category category : allCategories) {
            ProductView product = lowestPriceProducts.get(category);
            if (product != null) {
                categories.add(CategoryLowestPriceResponse.CategoryPrice.builder()
                        .category(category.getCode())
                        .brand(product.getBrandName())
                        .price(product.getPrice())
                        .build());
                totalPrice += product.getPrice();
            }
        }

        return CategoryLowestPriceResponse.builder()
                .categories(categories)
                .totalPrice(totalPrice)
                .build();
    }
    
    public CategoryPriceRangeResponse getCategoryPriceRangeInfo(CategoryCommandResponse categoryResponse) {
        Category category = categoryCommandPort.findByCode(categoryResponse.getCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryResponse.getCode()));
        
        // 해당 카테고리의 모든 상품 조회
        List<ProductView> products = productQueryPort.findByCategory(category.getId());
        
        if (products.isEmpty()) {
            throw new IllegalArgumentException("해당 카테고리에 상품이 없습니다: " + categoryResponse.getCode());
        }
        
        // 최저가 상품 찾기
        ProductView lowestPriceProduct = products.stream()
                .min(Comparator.comparingInt(ProductView::getPrice))
                .orElseThrow(() -> new IllegalArgumentException("최저가 상품을 찾을 수 없습니다."));
        
        // 최고가 상품 찾기
        ProductView highestPriceProduct = products.stream()
                .max(Comparator.comparingInt(ProductView::getPrice))
                .orElseThrow(() -> new IllegalArgumentException("최고가 상품을 찾을 수 없습니다."));
        
        // 응답 DTO 구성
        List<CategoryPriceRangeResponse.BrandPrice> lowestPrice = List.of(
                CategoryPriceRangeResponse.BrandPrice.builder()
                        .brandName(lowestPriceProduct.getBrandName())
                        .price(lowestPriceProduct.getPrice())
                        .build()
        );
        
        List<CategoryPriceRangeResponse.BrandPrice> highestPrice = List.of(
                CategoryPriceRangeResponse.BrandPrice.builder()
                        .brandName(highestPriceProduct.getBrandName())
                        .price(highestPriceProduct.getPrice())
                        .build()
        );
        
        return CategoryPriceRangeResponse.from(category, lowestPrice, highestPrice);
    }
} 