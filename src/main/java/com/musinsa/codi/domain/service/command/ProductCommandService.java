package com.musinsa.codi.domain.service.command;

import com.musinsa.codi.application.usecase.command.ProductCommandUseCase;
import com.musinsa.codi.common.annotation.PublishBrandEvent;
import com.musinsa.codi.common.annotation.PublishProductEvent;
import com.musinsa.codi.common.dto.command.ProductCommandRequest;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.event.BrandEventType;
import com.musinsa.codi.domain.event.ProductEventPublisher;
import com.musinsa.codi.domain.event.ProductEvent;
import com.musinsa.codi.domain.event.ProductEventType;
import com.musinsa.codi.domain.model.command.Brand;
import com.musinsa.codi.domain.model.command.Product;
import com.musinsa.codi.domain.port.command.BrandCommandPort;
import com.musinsa.codi.domain.port.command.CategoryCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandService implements ProductCommandUseCase {
    private final BrandCommandPort brandCommandPort;
    private final CategoryCommandPort categoryCommandPort;
    private final ProductEventPublisher productEventPublisher;

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    @PublishProductEvent(eventType = ProductEventType.CREATED)
    public Product addProduct(String brandName, ProductCommandRequest request) {
        Brand brand = findBrandByName(brandName);
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(categoryCommandPort.findByCode(request.getCategoryCode())
                        .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND)))
                .build();
        
        brand.addProduct(product);
        Brand savedBrand = brandCommandPort.save(brand);
        
        // 저장된 브랜드의 상품 목록에서 마지막 상품을 반환
        List<Product> savedProducts = savedBrand.getProducts();
        if (savedProducts.isEmpty()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return savedProducts.get(savedProducts.size() - 1);
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    @PublishProductEvent(eventType = ProductEventType.UPDATED)
    public Product updateProduct(String brandName, Long productId, ProductCommandRequest request) {
        Brand brand = findBrandByName(brandName);
        Product existingProduct = brand.findProductById(productId);
        
        // 기존 상품의 정보를 업데이트
        existingProduct.update(
            request.getName(), 
            request.getPrice(), 
            categoryCommandPort.findByCode(request.getCategoryCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND))
        );
        brandCommandPort.save(brand);
        
        return existingProduct;
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    public void deleteProduct(String brandName, Long productId) {
        Brand brand = findBrandByName(brandName);
        Product productToDelete = brand.findProductById(productId);
        
        // AOP에서 void 메서드의 Product 인자를 감지하지 못할 수 있으므로
        // 여기서는 직접 이벤트를 발행합니다
        log.debug("ProductCommandService: 상품 삭제 이벤트 직접 발행 - ID: {}, 이름: {}", 
                productToDelete.getId(), productToDelete.getName());
        productEventPublisher.publish(new ProductEvent(productToDelete, ProductEventType.DELETED));
        
        // 상품 삭제 처리
        brand.removeProduct(productId);
        brandCommandPort.save(brand);
    }

    private Brand findBrandByName(String name) {
        return brandCommandPort.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
    }
} 