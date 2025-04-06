package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.application.usecase.query.CategoryQueryUseCase;
import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.common.dto.query.CategoryLowestPriceResponse;
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
        return brandQueryPort.findByCategory(categoryResponse.toEntity());
    }

    public List<ProductView> getProductsByCategory(CategoryCommandResponse categoryResponse) {
        return productQueryPort.findByCategory(categoryResponse.toEntity());
    }

    public List<BrandView> getBrandsByPriceRange(CategoryCommandResponse categoryResponse, int minPrice, int maxPrice) {
        return brandQueryPort.findByPriceRange(categoryResponse.toEntity(), minPrice, maxPrice);
    }

    public List<ProductView> getProductsByPriceRange(CategoryCommandResponse categoryResponse, int minPrice, int maxPrice) {
        return productQueryPort.findByPriceRange(categoryResponse.toEntity(), minPrice, maxPrice);
    }

    @Override
    public CategoryLowestPriceResponse findLowestPricesByCategory() {
        // 모든 상품 뷰 조회
        List<ProductView> allProducts = productQueryPort.findAll();
        
        // 카테고리별로 그룹화하고 최저가 상품 찾기
        Map<Category, ProductView> lowestPriceProducts = allProducts.stream()
                .collect(Collectors.groupingBy(
                        ProductView::getCategory,
                        Collectors.minBy(Comparator.comparingInt(ProductView::getPrice))
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
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
} 