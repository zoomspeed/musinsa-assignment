package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.application.usecase.query.CategoryQueryUseCase;
import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.common.dto.query.CategoryResponse;
import com.musinsa.codi.common.dto.query.CategoryLowestPriceResponse;
import com.musinsa.codi.common.dto.query.CategoryPriceRangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryQueryController {
    private final CategoryQueryUseCase categoryQueryUseCase;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryQueryUseCase.getAllCategories());
    }

    @GetMapping("/lowest-price")
    public ResponseEntity<CategoryLowestPriceResponse> getLowestPricesByCategory() {
        return ResponseEntity.ok(categoryQueryUseCase.findLowestPricesByCategory());
    }
    
    @GetMapping("/price-range-info")
    public ResponseEntity<CategoryPriceRangeResponse> getCategoryPriceRangeInfo(
            @RequestParam String categoryCode) {
        return ResponseEntity.ok(categoryQueryUseCase.getCategoryPriceRangeInfo(categoryCode));
    }
} 