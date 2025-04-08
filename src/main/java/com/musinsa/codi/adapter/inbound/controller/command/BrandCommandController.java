package com.musinsa.codi.adapter.inbound.controller.command;

import com.musinsa.codi.application.usecase.command.BrandCommandUseCase;
import com.musinsa.codi.common.dto.command.BrandCommandRequest;
import com.musinsa.codi.common.dto.command.BrandCommandResponse;
import com.musinsa.codi.common.util.MessageUtils;
import com.musinsa.codi.domain.model.command.Brand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandCommandController {
    private final BrandCommandUseCase brandCommandUseCase;
    private final MessageUtils messageUtils;

    @PostMapping
    public ResponseEntity<BrandCommandResponse> createBrand(@Valid @RequestBody BrandCommandRequest request) {
        Brand brand = brandCommandUseCase.createBrand(request);
        return ResponseEntity.ok(BrandCommandResponse.builder()
                .success(true)
                .message(messageUtils.getMessage("success.brand.created", brand.getName()))
                .brandId(brand.getId())
                .build());
    }

    @PutMapping("/{brandName}")
    public ResponseEntity<BrandCommandResponse> updateBrand(
            @PathVariable String brandName,
            @Valid @RequestBody BrandCommandRequest request) {
        Brand brand = brandCommandUseCase.updateBrand(brandName, request);
        return ResponseEntity.ok(BrandCommandResponse.builder()
                .success(true)
                .message(messageUtils.getMessage("success.brand.updated", brandName, brand.getName()))
                .brandId(brand.getId())
                .build());
    }

    @DeleteMapping("/{brandName}")
    public ResponseEntity<BrandCommandResponse> deleteBrand(@PathVariable String brandName) {
        Brand brand = brandCommandUseCase.deleteBrand(brandName);
        return ResponseEntity.ok(BrandCommandResponse.builder()
                .success(true)
                .message(messageUtils.getMessage("success.brand.deleted", brandName))
                .brandId(brand.getId())
                .build());
    }
} 