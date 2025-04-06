package com.musinsa.codi.adapter.outbound.event;

import com.musinsa.codi.domain.event.BrandEvent;
import com.musinsa.codi.domain.event.BrandEventType;
import com.musinsa.codi.domain.model.query.BrandView;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrandEventListener {
    private final BrandQueryPort brandQueryPort;
    private final ProductQueryPort productQueryPort;

    @EventListener
    @Transactional
    public void handleBrandEvent(BrandEvent event) {
        switch (event.getEventType()) {
            case CREATED, UPDATED -> {
                BrandView brandView = BrandView.builder()
                        .id(event.getBrand().getId())
                        .name(event.getBrand().getName())
                        .build();
                brandQueryPort.save(brandView);
                
                // UPDATED 이벤트인 경우 연관된 상품 뷰들의 브랜드명도 업데이트
                if (event.getEventType() == BrandEventType.UPDATED) {
                    updateProductViews(event.getBrand().getId(), event.getBrand().getName());
                }
            }
            case DELETED -> {
                brandQueryPort.delete(event.getBrand().getId());
            }
        }
    }
    
    private void updateProductViews(Long brandId, String newBrandName) {
        log.info("BrandEventListener: 브랜드({})의 상품 뷰 브랜드명 업데이트 시작", brandId);
        List<ProductView> productViews = productQueryPort.findByBrandId(brandId);
        
        for (ProductView productView : productViews) {
            log.debug("BrandEventListener: 상품 뷰 업데이트 - ID: {}, 이전 브랜드명: {}, 새 브랜드명: {}", 
                    productView.getId(), productView.getBrandName(), newBrandName);
            productView.setBrandName(newBrandName);
            productQueryPort.save(productView);
        }
        
        log.info("BrandEventListener: 브랜드({})의 상품 뷰 {}개 업데이트 완료", brandId, productViews.size());
    }
} 