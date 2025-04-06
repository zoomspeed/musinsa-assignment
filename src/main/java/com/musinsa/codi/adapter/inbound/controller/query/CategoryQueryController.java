package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.application.usecase.query.CategoryQueryUseCase;
import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.common.dto.query.BrandQueryResponse;
import com.musinsa.codi.common.dto.query.CategoryResponse;
import com.musinsa.codi.common.dto.query.ProductQueryResponse;
import com.musinsa.codi.common.dto.query.CategoryLowestPriceResponse;
import com.musinsa.codi.domain.port.command.CategoryCommandPort;
import com.musinsa.codi.domain.service.query.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryQueryController {
    private final CategoryQueryService categoryQueryService;
    private final CategoryQueryUseCase categoryQueryUseCase;
    private final CategoryCommandPort categoryCommandPort;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryCommandPort.findAll().stream()
                .map(CategoryCommandResponse::from)
                .map(dto -> new CategoryResponse(dto.getCode(), dto.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryCode}/brands")
    public ResponseEntity<List<BrandQueryResponse>> getBrandsByCategory(
            @PathVariable String categoryCode) {
        CategoryCommandResponse categoryResponse = CategoryCommandResponse.from(
                categoryCommandPort.findByCode(categoryCode)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode)));
        List<BrandQueryResponse> responses = categoryQueryService.getBrandsByCategory(categoryResponse).stream()
                .map(BrandQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{categoryCode}/products")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByCategory(
            @PathVariable String categoryCode) {
        CategoryCommandResponse categoryResponse = CategoryCommandResponse.from(
                categoryCommandPort.findByCode(categoryCode)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode)));
        List<ProductQueryResponse> responses = categoryQueryService.getProductsByCategory(categoryResponse).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{categoryCode}/price-range")
    public ResponseEntity<List<BrandQueryResponse>> getBrandsByPriceRange(
            @PathVariable String categoryCode,
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        CategoryCommandResponse categoryResponse = CategoryCommandResponse.from(
                categoryCommandPort.findByCode(categoryCode)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode)));
        List<BrandQueryResponse> responses = categoryQueryService.getBrandsByPriceRange(categoryResponse, minPrice, maxPrice).stream()
                .map(BrandQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{categoryCode}/products/price-range")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByPriceRange(
            @PathVariable String categoryCode,
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        CategoryCommandResponse categoryResponse = CategoryCommandResponse.from(
                categoryCommandPort.findByCode(categoryCode)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode)));
        List<ProductQueryResponse> responses = categoryQueryService.getProductsByPriceRange(categoryResponse, minPrice, maxPrice).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/lowest-prices")
    public ResponseEntity<CategoryLowestPriceResponse> getLowestPricesByCategory() {
        return ResponseEntity.ok(categoryQueryUseCase.findLowestPricesByCategory());
    }
} 