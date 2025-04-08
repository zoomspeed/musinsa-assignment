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
import com.musinsa.codi.domain.service.query.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
        return CategoryCommandResponse.from(findCategoryByCode(categoryCode));
    }

    public List<BrandView> getBrandsByCategory(CategoryCommandResponse categoryResponse) {
        Category category = getCategory(categoryResponse);
        return brandQueryPort.findByCategory(category.getId());
    }

    public List<ProductView> getProductsByCategory(CategoryCommandResponse categoryResponse) {
        Category category = getCategory(categoryResponse);
        return productQueryPort.findByCategory(category.getId());
    }

    public List<BrandView> getBrandsByPriceRange(CategoryCommandResponse categoryResponse, int minPrice, int maxPrice) {
        Category category = getCategory(categoryResponse);
        return brandQueryPort.findByPriceRange(category, minPrice, maxPrice);
    }

    public List<ProductView> getProductsByPriceRange(CategoryCommandResponse categoryResponse, int minPrice, int maxPrice) {
        Category category = getCategory(categoryResponse);
        List<ProductView> products = productQueryPort.findByCategory(category.getId());
        return PriceCalculator.filterByPriceRange(products, minPrice, maxPrice);
    }

    @Override
    public CategoryPriceRangeResponse getCategoryPriceRangeInfo(String categoryCode) {
        Category category = findCategoryByCode(categoryCode);
        List<ProductView> products = getProductsWithValidation(category);

        ProductView lowestPriceProduct = PriceCalculator.findLowestPriceProduct(products)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_LOWEST_PRICE_NOT_FOUND));

        ProductView highestPriceProduct = PriceCalculator.findHighestPriceProduct(products)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_HIGHEST_PRICE_NOT_FOUND));

        return createPriceRangeResponse(category, lowestPriceProduct, highestPriceProduct);
    }

    @Override
    public CategoryLowestPriceResponse findLowestPricesByCategory() {
        Map<Long, Category> categoryMap = createCategoryMap();
        Map<Category, ProductView> lowestPriceProducts = findLowestPriceProductsByCategory(categoryMap);
        return createLowestPriceResponse(categoryMap.values(), lowestPriceProducts);
    }

    // 기존에 존재하는 Category 조회
    private Category findCategoryByCode(String categoryCode) {
        return categoryCommandPort.findByCode(categoryCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, categoryCode));
    }

    // CategoryCommandResponse에서 Category 엔티티를 가져오는 헬퍼 메서드
    private Category getCategory(CategoryCommandResponse categoryResponse) {
        return findCategoryByCode(categoryResponse.getCode());
    }

    private List<ProductView> getProductsWithValidation(Category category) {
        List<ProductView> products = productQueryPort.findByCategory(category.getId());
        if (products.isEmpty()) {
            throw new BusinessException(ErrorCode.CATEGORY_NO_PRODUCTS, category.getCode());
        }
        return products;
    }

    // BrandPrice 객체 생성 로직을 별도 메서드로 분리 (중복 제거)
    private CategoryPriceRangeResponse.BrandPrice createBrandPrice(ProductView product) {
        return CategoryPriceRangeResponse.BrandPrice.builder()
                .brandName(product.getBrandName())
                .price(product.getPrice())
                .build();
    }

    private CategoryPriceRangeResponse createPriceRangeResponse(Category category, ProductView lowest, ProductView highest) {
        List<CategoryPriceRangeResponse.BrandPrice> lowestPrice = List.of(createBrandPrice(lowest));
        List<CategoryPriceRangeResponse.BrandPrice> highestPrice = List.of(createBrandPrice(highest));
        return CategoryPriceRangeResponse.from(category, lowestPrice, highestPrice);
    }

    private Map<Long, Category> createCategoryMap() {
        return categoryCommandPort.findAll().stream()
                .collect(Collectors.toMap(Category::getId, category -> category));
    }

    private Map<Category, ProductView> findLowestPriceProductsByCategory(Map<Long, Category> categoryMap) {
        // 모든 ProductView를 가져온 후 그룹핑 및 최소 가격 제품 조회
        List<ProductView> allProducts = productQueryPort.findAll();
        Map<Category, ProductView> groupedLowestProducts = allProducts.stream()
                .collect(Collectors.groupingBy(
                        pv -> categoryMap.get(pv.getCategoryId())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> PriceCalculator.findLowestPriceProduct(entry.getValue()).orElse(null)
                ));

        // null 값 제거
        return groupedLowestProducts.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // CategoryLowestPriceResponse의 하위 CategoryPrice 객체를 생성하는 헬퍼 메서드
    private CategoryLowestPriceResponse.CategoryPrice createCategoryPrice(Category category, ProductView product) {
        return CategoryLowestPriceResponse.CategoryPrice.builder()
                .category(category.getCode())
                .brand(product.getBrandName())
                .price(product.getPrice())
                .build();
    }

    private CategoryLowestPriceResponse createLowestPriceResponse(Collection<Category> categories, Map<Category, ProductView> lowestPriceProducts) {
        List<CategoryLowestPriceResponse.CategoryPrice> categoryPrices = new ArrayList<>();
        int totalPrice = 0;

        for (Category category : categories) {
            ProductView product = lowestPriceProducts.get(category);
            if (product != null) {
                categoryPrices.add(createCategoryPrice(category, product));
                totalPrice += product.getPrice();
            }
        }

        return CategoryLowestPriceResponse.builder()
                .categories(categoryPrices)
                .totalPrice(totalPrice)
                .build();
    }
}