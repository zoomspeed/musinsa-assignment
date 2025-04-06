package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.common.dto.query.BrandQueryResponse;
import com.musinsa.codi.common.dto.query.CategoryResponse;
import com.musinsa.codi.common.dto.query.ProductQueryResponse;
import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.service.query.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryQueryController {
    private final CategoryQueryService categoryQueryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = Arrays.stream(Category.values())
                .map(category -> new CategoryResponse(category.name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{category}/brands")
    public ResponseEntity<List<BrandQueryResponse>> getBrandsByCategory(
            @PathVariable Category category) {
        List<BrandQueryResponse> responses = categoryQueryService.getBrandsByCategory(category).stream()
                .map(BrandQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{category}/products")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByCategory(
            @PathVariable Category category) {
        List<ProductQueryResponse> responses = categoryQueryService.getProductsByCategory(category).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{category}/price-range")
    public ResponseEntity<List<BrandQueryResponse>> getBrandsByPriceRange(
            @PathVariable Category category,
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        List<BrandQueryResponse> responses = categoryQueryService.getBrandsByPriceRange(category, minPrice, maxPrice).stream()
                .map(BrandQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{category}/products/price-range")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByPriceRange(
            @PathVariable Category category,
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        List<ProductQueryResponse> responses = categoryQueryService.getProductsByPriceRange(category, minPrice, maxPrice).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
} 