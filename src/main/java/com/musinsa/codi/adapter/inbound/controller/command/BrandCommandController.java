package com.musinsa.codi.adapter.inbound.controller.command;

import com.musinsa.codi.common.dto.command.BrandCommandRequest;
import com.musinsa.codi.common.dto.command.BrandCommandResponse;
import com.musinsa.codi.common.dto.command.ProductCommandRequest;
import com.musinsa.codi.common.dto.command.ProductCommandResponse;
import com.musinsa.codi.domain.model.command.Brand;
import com.musinsa.codi.domain.service.command.BrandCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandCommandController {
    private final BrandCommandService brandCommandService;

    @PostMapping
    public ResponseEntity<BrandCommandResponse> createBrand(@Valid @RequestBody BrandCommandRequest request) {
        Brand brand = brandCommandService.createBrand(request);
        return ResponseEntity.ok(BrandCommandResponse.builder()
                .success(true)
                .message("브랜드가 성공적으로 생성되었습니다.")
                .brandId(brand.getId())
                .build());
    }

    @PostMapping("/{brandName}/products")
    public ResponseEntity<ProductCommandResponse> addProduct(
            @PathVariable String brandName,
            @Valid @RequestBody ProductCommandRequest request) {
        Brand brand = brandCommandService.addProduct(brandName, request);
        return ResponseEntity.ok(ProductCommandResponse.builder()
                .success(true)
                .message("상품이 성공적으로 추가되었습니다.")
                .productId(brand.getProducts().get(brand.getProducts().size() - 1).getId())
                .brandId(brand.getId())
                .build());
    }

    @PutMapping("/{brandName}/products/{productId}")
    public ResponseEntity<ProductCommandResponse> updateProduct(
            @PathVariable String brandName,
            @PathVariable Long productId,
            @Valid @RequestBody ProductCommandRequest request) {
        Brand brand = brandCommandService.updateProduct(brandName, productId, request);
        return ResponseEntity.ok(ProductCommandResponse.builder()
                .success(true)
                .message("상품이 성공적으로 업데이트되었습니다.")
                .productId(productId)
                .brandId(brand.getId())
                .build());
    }

    @DeleteMapping("/{brandName}/products/{productId}")
    public ResponseEntity<ProductCommandResponse> deleteProduct(
            @PathVariable String brandName,
            @PathVariable Long productId) {
        brandCommandService.deleteProduct(brandName, productId);
        return ResponseEntity.ok(ProductCommandResponse.builder()
                .success(true)
                .message("상품이 성공적으로 삭제되었습니다.")
                .productId(productId)
                .build());
    }

    @DeleteMapping("/{brandName}")
    public ResponseEntity<BrandCommandResponse> deleteBrand(@PathVariable String brandName) {
        brandCommandService.deleteBrand(brandName);
        return ResponseEntity.ok(BrandCommandResponse.builder()
                .success(true)
                .message("브랜드가 성공적으로 삭제되었습니다.")
                .build());
    }
} 