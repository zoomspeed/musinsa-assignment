package com.musinsa.codi.domain.service.command;

import com.musinsa.codi.common.dto.command.BrandCommandRequest;
import com.musinsa.codi.common.dto.command.ProductCommandRequest;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.event.BrandEvent;
import com.musinsa.codi.domain.event.BrandEventPublisher;
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
public class BrandCommandService {
    private final BrandCommandPort brandCommandPort;
    private final BrandEventPublisher brandEventPublisher;

    public Brand createBrand(BrandCommandRequest request) {
        if (brandCommandPort.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.BRAND_ALREADY_EXISTS);
        }
        Brand brand = Brand.builder()
                .name(request.getName())
                .build();
        brand = brandCommandPort.save(brand);
        brandEventPublisher.publish(new BrandEvent(brand, BrandEventType.CREATED));
        return brand;
    }

    public Brand updateBrand(String brandName, BrandCommandRequest request) {
        Brand brand = brandCommandPort.findByName(brandName)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        
        if (!brand.getName().equals(request.getName()) && brandCommandPort.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.BRAND_ALREADY_EXISTS);
        }
        
        brand.updateName(request.getName());
        brand = brandCommandPort.save(brand);
        brandEventPublisher.publish(new BrandEvent(brand, BrandEventType.UPDATED));
        return brand;
    }

    public Brand addProduct(String brandName, ProductCommandRequest request) {
        Brand brand = brandCommandPort.findByName(brandName)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        
        Product product = request.toProduct();
        brand.addProduct(product);
        brand = brandCommandPort.save(brand);
        brandEventPublisher.publish(new BrandEvent(brand, BrandEventType.UPDATED));
        return brand;
    }

    public Brand updateProduct(String brandName, Long productId, ProductCommandRequest request) {
        Brand brand = brandCommandPort.findByName(brandName)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        
        Product product = brand.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        product.updatePrice(request.getPrice());
        brand = brandCommandPort.save(brand);
        brandEventPublisher.publish(new BrandEvent(brand, BrandEventType.UPDATED));
        return brand;
    }

    public void deleteProduct(String brandName, Long productId) {
        Brand brand = brandCommandPort.findByName(brandName)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        
        Product product = brand.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        brand.removeProduct(product);
        brand = brandCommandPort.save(brand);
        brandEventPublisher.publish(new BrandEvent(brand, BrandEventType.UPDATED));
    }

    public void deleteBrand(String brandName) {
        Brand brand = brandCommandPort.findByName(brandName)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        brandEventPublisher.publish(new BrandEvent(brand, BrandEventType.DELETED));
        brandCommandPort.delete(brand.getId());
    }
} 