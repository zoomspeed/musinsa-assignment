package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.common.dto.query.ProductQueryResponse;
import com.musinsa.codi.domain.port.command.CategoryCommandPort;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQueryController {
    private final ProductQueryPort productQueryPort;
    private final CategoryCommandPort categoryCommandPort;

    @GetMapping
    public ResponseEntity<List<ProductQueryResponse>> getAllProducts() {
        return ResponseEntity.ok(productQueryPort.findAll().stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductQueryResponse> getProductById(@PathVariable Long id) {
        return productQueryPort.findById(id)
                .map(ProductQueryResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByCategory(@RequestParam String categoryCode) {
        Long categoryId = categoryCommandPort.findByCode(categoryCode)
                .map(category -> category.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode));
        return ResponseEntity.ok(productQueryPort.findByCategory(categoryId).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/category/price-range")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByPriceRange(
            @RequestParam String categoryCode,
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        Long categoryId = categoryCommandPort.findByCode(categoryCode)
                .map(category -> category.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode));
        return ResponseEntity.ok(productQueryPort.findByPriceRange(categoryId, minPrice, maxPrice).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByBrandId(@PathVariable Long brandId) {
        return ResponseEntity.ok(productQueryPort.findByBrandId(brandId).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList()));
    }
} 