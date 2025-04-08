package com.musinsa.codi.adapter.outbound.event;

import com.musinsa.codi.domain.event.ProductEvent;
import com.musinsa.codi.domain.event.ProductEventType;
import com.musinsa.codi.domain.model.command.Product;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener extends AbstractDomainEventListener<ProductEvent> {
    private final ProductQueryPort productQueryPort;
    private final BrandQueryPort brandQueryPort;

    @EventListener
    public void onProductEvent(ProductEvent event) {
        handleEvent(event);
    }

    @Override
    protected void processEvent(ProductEvent event) {
        ProductEventType eventType = (ProductEventType) event.getEventType();
        switch (eventType) {
            case CREATED:
                handleCreateEvent(event.getProduct());
                break;
            case UPDATED:
                handleUpdateEvent(event.getProduct());
                break;
            case DELETED:
                handleDeleteEvent(event.getProduct());
                break;
        }
    }

    private void handleCreateEvent(Product product) {
        saveProductView(product, false);
    }

    private void handleUpdateEvent(Product product) {
        saveProductView(product, true);
    }

    private void handleDeleteEvent(Product product) {
        productQueryPort.findByProductId(product.getId())
            .forEach(productView -> {
                productQueryPort.deleteById(productView.getId());
                log.debug("상품 뷰 삭제 완료 - ID: {}", productView.getId());
            });
    }

    private void saveProductView(Product product, boolean isUpdate) {
        String brandName = getBrandName(product);
        ProductView productView = createProductView(product, brandName, isUpdate);
        saveAndLogProductView(productView, isUpdate);
    }

    private String getBrandName(Product product) {
        return brandQueryPort.findById(product.getBrand().getId())
            .map(brandView -> {
                log.debug("브랜드 조회 성공 - ID: {}, 이름: {}", brandView.getId(), brandView.getName());
                return brandView.getName();
            })
            .orElseGet(() -> {
                log.warn("브랜드를 찾을 수 없음 - ID: {}", product.getBrand().getId());
                return "Unknown Brand";
            });
    }

    private ProductView createProductView(Product product, String brandName, boolean isUpdate) {
        if (isUpdate) {
            return updateExistingProductView(product, brandName);
        }
        return createNewProductView(product, brandName);
    }

    private ProductView createNewProductView(Product product, String brandName) {
        return ProductView.builder()
                .productId(product.getId())
                .productName(product.getName())
                .brandId(product.getBrand().getId())
                .brandName(brandName)
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .categoryCode(product.getCategory().getCode())
                .price(product.getPrice())
                .build();
    }

    private ProductView updateExistingProductView(Product product, String brandName) {
        List<ProductView> existingViews = productQueryPort.findByProductId(product.getId());
        if (existingViews.isEmpty()) {
            return createNewProductView(product, brandName);
        }

        ProductView existingView = existingViews.get(0);
        return ProductView.builder()
                .id(existingView.getId())
                .productId(existingView.getProductId())
                .productName(product.getName())
                .brandId(product.getBrand().getId())
                .brandName(brandName)
                .categoryId(existingView.getCategoryId())
                .categoryName(existingView.getCategoryName())
                .categoryCode(existingView.getCategoryCode())
                .price(product.getPrice())
                .build();
    }

    private void saveAndLogProductView(ProductView productView, boolean isUpdate) {
        try {
            ProductView savedView = productQueryPort.save(productView);
            log.debug("상품 뷰 저장 성공 - ID: {}, ProductID: {}, 카테고리: {}, 이벤트 타입: {}",
                    savedView.getId(), savedView.getProductId(), savedView.getCategoryCode(),
                    isUpdate ? "UPDATE" : "CREATE");
        } catch (Exception e) {
            log.error("상품 뷰 저장 중 오류 발생 - ProductID: {}, 카테고리: {}", 
                    productView.getProductId(), productView.getCategoryCode(), e);
            throw e;
        }
    }
} 