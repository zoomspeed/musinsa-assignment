package com.musinsa.codi.adapter.outbound.event;

import com.musinsa.codi.domain.event.ProductEvent;
import com.musinsa.codi.domain.event.ProductEventType;
import com.musinsa.codi.domain.model.command.Product;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductEventListener {
    private final ProductQueryPort productQueryPort;
    private final BrandQueryPort brandQueryPort;

    @EventListener
    @Transactional
    public void handleProductEvent(ProductEvent event) {
        Product product = event.getProduct();
        
        if (event.getEventType() == ProductEventType.DELETED) {
            // 상품이 삭제된 경우, 해당 상품 뷰도 삭제
            productQueryPort.delete(product.getId());
            return;
        }
        
        // 브랜드 정보 조회
        String brandName = brandQueryPort.findById(product.getBrand().getId())
                .map(brandView -> brandView.getName())
                .orElse("Unknown Brand");
        
        // 상품 뷰 생성 또는 업데이트
        ProductView productView = ProductView.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .brandId(product.getBrand().getId())
                .brandName(brandName)
                .build();
        
        productQueryPort.save(productView);
    }
} 