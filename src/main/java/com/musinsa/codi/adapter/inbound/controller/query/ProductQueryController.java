package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
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

    @GetMapping("/category/{categoryCode}")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByCategory(@PathVariable String categoryCode) {
        CategoryCommandResponse categoryResponse = CategoryCommandResponse.from(
                categoryCommandPort.findByCode(categoryCode)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode)));
        return ResponseEntity.ok(productQueryPort.findByCategory(categoryResponse.toEntity().getId()).stream()
                .map(ProductQueryResponse::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/category/{categoryCode}/price-range")
    public ResponseEntity<List<ProductQueryResponse>> getProductsByPriceRange(
            @PathVariable String categoryCode,
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        CategoryCommandResponse categoryResponse = CategoryCommandResponse.from(
                categoryCommandPort.findByCode(categoryCode)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode)));
        return ResponseEntity.ok(productQueryPort.findByPriceRange(categoryResponse.toEntity().getId(), minPrice, maxPrice).stream()
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