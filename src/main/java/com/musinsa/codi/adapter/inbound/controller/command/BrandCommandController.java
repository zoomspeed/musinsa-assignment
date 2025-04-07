package com.musinsa.codi.adapter.inbound.controller.command;

import com.musinsa.codi.application.usecase.command.BrandCommandUseCase;
import com.musinsa.codi.common.dto.command.BrandCommandRequest;
import com.musinsa.codi.common.dto.command.BrandCommandResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandCommandController {
    private final BrandCommandUseCase brandCommandUseCase;

    @PostMapping
    public ResponseEntity<BrandCommandResponse> createBrand(@Valid @RequestBody BrandCommandRequest request) {
        Long brandId = brandCommandUseCase.createBrand(request).getId();
        return ResponseEntity.ok(BrandCommandResponse.builder()
                .success(true)
                .message("브랜드가 성공적으로 생성되었습니다.")
                .brandId(brandId)
                .build());
    }

    @PutMapping("/{brandName}")
    public ResponseEntity<BrandCommandResponse> updateBrand(
            @PathVariable String brandName,
            @Valid @RequestBody BrandCommandRequest request) {
        Long brandId = brandCommandUseCase.updateBrand(brandName, request).getId();
        return ResponseEntity.ok(BrandCommandResponse.builder()
                .success(true)
                .message("브랜드명이 성공적으로 변경되었습니다.")
                .brandId(brandId)
                .build());
    }

    @DeleteMapping("/{brandName}")
    public ResponseEntity<BrandCommandResponse> deleteBrand(@PathVariable String brandName) {
        Long brandId = brandCommandUseCase.deleteBrand(brandName).getId();
        return ResponseEntity.ok(BrandCommandResponse.builder()
                .success(true)
                .message("브랜드가 성공적으로 삭제되었습니다.")
                .brandId(brandId)
                .build());
    }
} 