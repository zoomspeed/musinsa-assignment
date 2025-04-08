package com.musinsa.codi.adapter.outbound.event;

import com.musinsa.codi.domain.event.BrandEvent;
import com.musinsa.codi.domain.event.BrandEventType;
import com.musinsa.codi.domain.model.command.Brand;
import com.musinsa.codi.domain.model.query.BrandView;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrandEventListener extends AbstractDomainEventListener<BrandEvent> {
    private final BrandQueryPort brandQueryPort;
    private final ProductQueryPort productQueryPort;

    @EventListener
    public void onBrandEvent(BrandEvent event) {
        handleEvent(event);
    }

    @Override
    protected void processEvent(BrandEvent event) {
        BrandEventType eventType = (BrandEventType) event.getEventType();
        switch (eventType) {
            case CREATED:
                handleCreateEvent(event.getBrand());
                break;
            case UPDATED:
                handleUpdateEvent(event.getBrand());
                break;
            case DELETED:
                handleDeleteEvent(event.getBrand());
                break;
        }
    }

    private void handleCreateEvent(Brand brand) {
        saveBrandView(brand);
    }

    private void handleUpdateEvent(Brand brand) {
        saveBrandView(brand);
        updateRelatedProductViews(brand);
    }

    private void handleDeleteEvent(Brand brand) {
        brandQueryPort.delete(brand.getId());
    }

    private void saveBrandView(Brand brand) {
        BrandView brandView = createBrandView(brand);
        brandQueryPort.save(brandView);
        log.debug("브랜드 뷰 저장 완료 - ID: {}, 이름: {}", brandView.getId(), brandView.getName());
    }

    private BrandView createBrandView(Brand brand) {
        return BrandView.builder()
                .id(brand.getId())
                .name(brand.getName())
                .build();
    }

    private void updateRelatedProductViews(Brand brand) {
        List<ProductView> productViews = productQueryPort.findByBrandId(brand.getId());
        
        productViews.forEach(productView -> {
            ProductView updatedView = updateProductViewBrandName(productView, brand.getName());
            productQueryPort.save(updatedView);
            log.debug("상품 뷰 브랜드명 업데이트 - ID: {}, 이전: {}, 새로운: {}", 
                    productView.getId(), productView.getBrandName(), brand.getName());
        });
        
        log.debug("브랜드({})의 상품 뷰 {}개 업데이트 완료", brand.getId(), productViews.size());
    }

    private ProductView updateProductViewBrandName(ProductView productView, String newBrandName) {
        return ProductView.builder()
                .id(productView.getId())
                .productId(productView.getProductId())
                .productName(productView.getProductName())
                .brandId(productView.getBrandId())
                .brandName(newBrandName)
                .categoryId(productView.getCategoryId())
                .categoryCode(productView.getCategoryCode())
                .categoryName(productView.getCategoryName())
                .price(productView.getPrice())
                .build();
    }
} 