package com.musinsa.codi.domain.service.command;

import com.musinsa.codi.application.usecase.command.ProductCommandUseCase;
import com.musinsa.codi.common.annotation.PublishBrandEvent;
import com.musinsa.codi.common.dto.command.ProductCommandRequest;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.event.BrandEventType;
import com.musinsa.codi.domain.event.ProductEvent;
import com.musinsa.codi.domain.event.ProductEventPublisher;
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
    private final ProductEventPublisher productEventPublisher;

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    public Brand addProduct(String brandName, ProductCommandRequest request) {
        Brand brand = findBrandByName(brandName);
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
        brand.addProduct(product);
        Brand savedBrand = brandCommandPort.save(brand);
        
        // 저장된 상품 찾기 (ID 획득을 위해)
        Product savedProduct = savedBrand.findProductByName(request.getName());
        // 상품 생성 이벤트 발행
        productEventPublisher.publish(new ProductEvent(savedProduct, ProductEventType.CREATED));
        
        return savedBrand;
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    public Brand updateProduct(String brandName, Long productId, ProductCommandRequest request) {
        Brand brand = findBrandByName(brandName);
        Product product = brand.findProductById(productId);
        product.update(request.getName(), request.getPrice(), request.getCategory());
        Brand savedBrand = brandCommandPort.save(brand);
        
        // 상품 업데이트 이벤트 발행
        Product updatedProduct = savedBrand.findProductById(productId);
        productEventPublisher.publish(new ProductEvent(updatedProduct, ProductEventType.UPDATED));
        
        return savedBrand;
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    public Brand deleteProduct(String brandName, Long productId) {
        Brand brand = findBrandByName(brandName);
        Product productToDelete = brand.findProductById(productId);
        
        // 상품 삭제 전에 이벤트를 발행해야 함 (삭제 후에는 참조할 수 없음)
        productEventPublisher.publish(new ProductEvent(productToDelete, ProductEventType.DELETED));
        
        brand.removeProduct(productId);
        return brandCommandPort.save(brand);
    }

    private Brand findBrandByName(String name) {
        return brandCommandPort.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
    }
} 