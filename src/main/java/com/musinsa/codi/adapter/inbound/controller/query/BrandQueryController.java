package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.common.dto.query.BrandQueryResponse;
import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.service.query.BrandQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandQueryController {
    private final BrandQueryService brandQueryService;

    @GetMapping
    public ResponseEntity<List<BrandQueryResponse>> getAllBrands() {
        List<BrandQueryResponse> responses = brandQueryService.getAllBrands().stream()
                .map(BrandQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{brandName}")
    public ResponseEntity<BrandQueryResponse> getBrandByName(@PathVariable String brandName) {
        BrandQueryResponse response = BrandQueryResponse.from(brandQueryService.getBrandByName(brandName));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<BrandQueryResponse>> getBrandsByCategory(
            @PathVariable Category category) {
        List<BrandQueryResponse> responses = brandQueryService.getBrandsByCategory(category).stream()
                .map(BrandQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/category/{category}/price-range")
    public ResponseEntity<List<BrandQueryResponse>> getBrandsByPriceRange(
            @PathVariable Category category,
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        List<BrandQueryResponse> responses = brandQueryService.getBrandsByPriceRange(category, minPrice, maxPrice).stream()
                .map(BrandQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
} 