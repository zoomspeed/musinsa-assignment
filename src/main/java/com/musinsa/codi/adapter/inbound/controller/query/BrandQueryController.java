package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.application.usecase.query.BrandQueryUseCase;
import com.musinsa.codi.common.dto.query.BrandLowestPriceResponse;
import com.musinsa.codi.common.dto.query.BrandQueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandQueryController {
    private final BrandQueryUseCase brandQueryUseCase;

    @GetMapping
    public ResponseEntity<List<BrandQueryResponse>> getAllBrands() {
        List<BrandQueryResponse> responses = brandQueryUseCase.getAllBrands().stream()
                .map(BrandQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{brandName}")
    public ResponseEntity<BrandQueryResponse> getBrandByName(@PathVariable String brandName) {
        BrandQueryResponse response = BrandQueryResponse.from(brandQueryUseCase.getBrandByName(brandName));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lowest-price")
    public ResponseEntity<BrandLowestPriceResponse> getLowestTotalPriceBrand() {
        return ResponseEntity.ok(brandQueryUseCase.findLowestPricesByCategory());
    }
}