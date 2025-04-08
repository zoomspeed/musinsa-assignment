package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.application.usecase.query.CategoryQueryUseCase;
import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.common.dto.query.CategoryLowestPriceResponse;
import com.musinsa.codi.common.dto.query.CategoryPriceRangeResponse;
import com.musinsa.codi.common.dto.query.CategoryResponse;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
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

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryCommandPort.findAll().stream()
                .map(CategoryCommandResponse::from)
                .map(dto -> new CategoryResponse(dto.getCode(), dto.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryCommandResponse getCategoryByCode(String categoryCode) {
        return CategoryCommandResponse.from(
                categoryCommandPort.findByCode(categoryCode)
                        .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryCode)));
    }

    public List<BrandView> getBrandsByCategory(CategoryCommandResponse categoryResponse) {
        Category category = categoryCommandPort.findByCode(categoryResponse.getCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryResponse.getCode()));
        return brandQueryPort.findByCategory(category.getId());
    }

    public List<ProductView> getProductsByCategory(CategoryCommandResponse categoryResponse) {
        Category category = categoryCommandPort.findByCode(categoryResponse.getCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryResponse.getCode()));
        return productQueryPort.findByCategory(category.getId());
    }

    public List<BrandView> getBrandsByPriceRange(CategoryCommandResponse categoryResponse, int minPrice, int maxPrice) {
        Category category = categoryCommandPort.findByCode(categoryResponse.getCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryResponse.getCode()));
        return brandQueryPort.findByPriceRange(category, minPrice, maxPrice);
    }

    public List<ProductView> getProductsByPriceRange(CategoryCommandResponse categoryResponse, int minPrice, int maxPrice) {
        Category category = categoryCommandPort.findByCode(categoryResponse.getCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryResponse.getCode()));
        return productQueryPort.findByPriceRange(category.getId(), minPrice, maxPrice);
    }

    @Override
    public CategoryPriceRangeResponse getCategoryPriceRangeInfo(String categoryCode) {
        Category category = categoryCommandPort.findByCode(categoryCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryCode));
        
        List<ProductView> products = productQueryPort.findByCategory(category.getId());
        
        if (products.isEmpty()) {
            throw new BusinessException(ErrorCode.CATEGORY_NO_PRODUCTS, categoryCode);
        }
        
        ProductView lowestPriceProduct = products.stream()
                .min(Comparator.comparingInt(ProductView::getPrice))
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_LOWEST_PRICE_NOT_FOUND));
        
        ProductView highestPriceProduct = products.stream()
                .max(Comparator.comparingInt(ProductView::getPrice))
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_HIGHEST_PRICE_NOT_FOUND));
        
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

    @Override
    public CategoryLowestPriceResponse findLowestPricesByCategory() {
        List<ProductView> allProducts = productQueryPort.findAll();
        
        Map<Long, Category> categoryMap = categoryCommandPort.findAll().stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

        Map<Category, ProductView> lowestPriceProducts = allProducts.stream()
                .collect(Collectors.groupingBy(
                        pv -> categoryMap.get(pv.getCategoryId()),
                        Collectors.minBy(Comparator.comparingInt(ProductView::getPrice))
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue().get()
                ));

        List<CategoryLowestPriceResponse.CategoryPrice> categories = new ArrayList<>();
        int totalPrice = 0;

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