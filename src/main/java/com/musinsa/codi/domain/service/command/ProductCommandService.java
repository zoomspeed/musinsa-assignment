package com.musinsa.codi.domain.service.command;

import com.musinsa.codi.application.usecase.command.ProductCommandUseCase;
import com.musinsa.codi.common.annotation.PublishBrandEvent;
import com.musinsa.codi.common.dto.command.ProductCommandRequest;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.event.BrandEventType;
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
    public Brand addProduct(String brandName, ProductCommandRequest request) {
        Brand brand = findBrandByName(brandName);
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
        brand.addProduct(product);
        return brandCommandPort.save(brand);
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    public Brand updateProduct(String brandName, Long productId, ProductCommandRequest request) {
        Brand brand = findBrandByName(brandName);
        Product product = brand.findProductById(productId);
        product.update(request.getName(), request.getPrice(), request.getCategory());
        return brandCommandPort.save(brand);
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    public Brand deleteProduct(String brandName, Long productId) {
        Brand brand = findBrandByName(brandName);
        brand.removeProduct(productId);
        return brandCommandPort.save(brand);
    }

    private Brand findBrandByName(String name) {
        return brandCommandPort.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
    }
} 