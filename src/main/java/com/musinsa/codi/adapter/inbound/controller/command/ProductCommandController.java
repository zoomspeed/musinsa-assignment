package com.musinsa.codi.adapter.inbound.controller.command;

import com.musinsa.codi.application.usecase.command.ProductCommandUseCase;
import com.musinsa.codi.common.dto.command.ProductCommandRequest;
import com.musinsa.codi.common.dto.command.ProductCommandResponse;
import com.musinsa.codi.domain.model.command.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands/{brandName}/products")
@RequiredArgsConstructor
public class ProductCommandController {
    private final ProductCommandUseCase productCommandUseCase;

    @PostMapping
    public ResponseEntity<ProductCommandResponse> addProduct(
            @PathVariable String brandName,
            @Valid @RequestBody ProductCommandRequest request) {
        Product product = productCommandUseCase.addProduct(brandName, request);
        return ResponseEntity.ok(ProductCommandResponse.builder()
                .success(true)
                .message("상품이 성공적으로 추가되었습니다.")
                .productId(product.getId())
                .build());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductCommandResponse> updateProduct(
            @PathVariable String brandName,
            @PathVariable Long productId,
            @Valid @RequestBody ProductCommandRequest request) {
        Product product = productCommandUseCase.updateProduct(brandName, productId, request);
        return ResponseEntity.ok(ProductCommandResponse.builder()
                .success(true)
                .message("상품이 성공적으로 업데이트되었습니다.")
                .productId(product.getId())
                .build());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductCommandResponse> deleteProduct(
            @PathVariable String brandName,
            @PathVariable Long productId) {
        productCommandUseCase.deleteProduct(brandName, productId);
        return ResponseEntity.ok(ProductCommandResponse.builder()
                .success(true)
                .message("상품이 성공적으로 삭제되었습니다.")
                .productId(productId)
                .build());
    }
} 