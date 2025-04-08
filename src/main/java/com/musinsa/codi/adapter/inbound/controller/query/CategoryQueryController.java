package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.application.usecase.query.CategoryQueryUseCase;
import com.musinsa.codi.common.dto.command.CategoryCommandResponse;
import com.musinsa.codi.common.dto.query.CategoryResponse;
import com.musinsa.codi.common.dto.query.CategoryLowestPriceResponse;
import com.musinsa.codi.common.dto.query.CategoryPriceRangeResponse;
import com.musinsa.codi.domain.port.command.CategoryCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryQueryController {
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

    @GetMapping("/lowest-prices")
    public ResponseEntity<CategoryLowestPriceResponse> getLowestPricesByCategory() {
        return ResponseEntity.ok(categoryQueryUseCase.findLowestPricesByCategory());
    }
    
    @GetMapping("/price-range-info")
    public ResponseEntity<CategoryPriceRangeResponse> getCategoryPriceRangeInfo(
            @RequestParam String categoryCode) {
        CategoryCommandResponse categoryResponse = CategoryCommandResponse.from(
                categoryCommandPort.findByCode(categoryCode)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 코드입니다: " + categoryCode)));
        return ResponseEntity.ok(categoryQueryUseCase.getCategoryPriceRangeInfo(categoryResponse));
    }
} 