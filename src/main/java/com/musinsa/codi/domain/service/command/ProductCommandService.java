package com.musinsa.codi.domain.service.command;

import com.musinsa.codi.application.usecase.command.ProductCommandUseCase;
import com.musinsa.codi.common.annotation.PublishBrandEvent;
import com.musinsa.codi.common.annotation.PublishProductEvent;
import com.musinsa.codi.common.dto.command.ProductCommandRequest;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.event.BrandEventType;
import com.musinsa.codi.domain.event.ProductEventType;
import com.musinsa.codi.domain.model.command.Brand;
import com.musinsa.codi.domain.model.command.Product;
import com.musinsa.codi.domain.port.command.BrandCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandService implements ProductCommandUseCase {
    private final BrandCommandPort brandCommandPort;

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    @PublishProductEvent(eventType = ProductEventType.CREATED)
    public Product addProduct(String brandName, ProductCommandRequest request) {
        Brand brand = findBrandByName(brandName);
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
        brand.addProduct(product);
        Brand savedBrand = brandCommandPort.save(brand);
        
        // 저장된 상품 찾기 (ID 획득을 위해)
        return savedBrand.findProductByName(request.getName());
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    @PublishProductEvent(eventType = ProductEventType.UPDATED)
    public Product updateProduct(String brandName, Long productId, ProductCommandRequest request) {
        Brand brand = findBrandByName(brandName);
        Product product = brand.findProductById(productId);
        product.update(request.getName(), request.getPrice(), request.getCategory());
        Brand savedBrand = brandCommandPort.save(brand);
        
        // 업데이트된 상품 반환
        return savedBrand.findProductById(productId);
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    @PublishProductEvent(eventType = ProductEventType.DELETED)
    public void deleteProduct(String brandName, Long productId) {
        Brand brand = findBrandByName(brandName);
        Product productToDelete = brand.findProductById(productId);
        
        // 상품 삭제 처리 (AOP가 productToDelete를 자동으로 이벤트 발행에 사용)
        brand.removeProduct(productId);
        brandCommandPort.save(brand);
    }

    private Brand findBrandByName(String name) {
        return brandCommandPort.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
    }
} 