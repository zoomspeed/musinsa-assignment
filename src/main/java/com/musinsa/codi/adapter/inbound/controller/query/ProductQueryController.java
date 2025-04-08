package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.application.usecase.query.ProductQueryUseCase;
import com.musinsa.codi.common.dto.query.ProductQueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQueryController {
    private final ProductQueryUseCase productQueryUseCase;

    @GetMapping
    public ResponseEntity<List<ProductQueryResponse>> getAllProducts() {
        return ResponseEntity.ok(productQueryUseCase.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductQueryResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productQueryUseCase.getProductById(id));
    }

    @GetMapping("/category")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByCategory(@RequestParam String categoryCode) {
        return ResponseEntity.ok(productQueryUseCase.getProductsByCategory(categoryCode));
    }

    @GetMapping("/category/price-range")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByPriceRange(
            @RequestParam String categoryCode,
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        return ResponseEntity.ok(productQueryUseCase.getProductsByPriceRange(categoryCode, minPrice, maxPrice));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByBrandId(@PathVariable Long brandId) {
        return ResponseEntity.ok(productQueryUseCase.getProductsByBrandId(brandId));
    }
} 