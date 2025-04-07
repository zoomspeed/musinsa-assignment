package com.musinsa.codi.adapter.outbound.event;

import com.musinsa.codi.domain.event.ProductEvent;
import com.musinsa.codi.domain.model.command.Category;
import com.musinsa.codi.domain.model.command.Product;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {
    private final ProductQueryPort productQueryPort;
    private final BrandQueryPort brandQueryPort;

    @EventListener
    @Transactional
    public void handleProductEvent(ProductEvent event) {
        Product product = event.getProduct();
        log.info("ProductEventListener: 이벤트 수신 - 타입: {}, 상품 ID: {}, 이름: {}",
                event.getEventType(), product.getId(), product.getName());
        
        switch (event.getEventType()) {
            case CREATED:
                handleCreateEvent(product);
                break;
            case UPDATED:
                handleUpdateEvent(product);
                break;
            case DELETED:
                handleDeleteEvent(product);
                break;
            default:
                log.warn("ProductEventListener: 알 수 없는 이벤트 타입 - {}", event.getEventType());
        }
    }

    private void handleCreateEvent(Product product) {
        log.info("ProductEventListener: 상품 생성 이벤트 처리 - ID: {}, 이름: {}", product.getId(), product.getName());
        saveProductView(product, false);
    }

    private void handleUpdateEvent(Product product) {
        log.info("ProductEventListener: 상품 업데이트 이벤트 처리 - ID: {}, 이름: {}", product.getId(), product.getName());
        saveProductView(product, true);
    }

    private void handleDeleteEvent(Product product) {
        log.info("ProductEventListener: 상품 삭제 이벤트 처리 - ID: {}", product.getId());
        List<ProductView> productViews = productQueryPort.findByProductId(product.getId());
        for (ProductView productView : productViews) {
            productQueryPort.deleteById(productView.getId());
            log.info("ProductEventListener: 상품 뷰 삭제 완료 - ID: {}", productView.getId());
        }
    }

    private void saveProductView(Product product, boolean isUpdate) {
        // 브랜드 정보 조회
        String brandName = brandQueryPort.findById(product.getBrand().getId())
                .map(brandView -> {
                    log.info("ProductEventListener: 브랜드 조회 성공 - ID: {}, 이름: {}",
                            brandView.getId(), brandView.getName());
                    return brandView.getName();
                })
                .orElse("Unknown Brand");
        
        if ("Unknown Brand".equals(brandName)) {
            log.warn("ProductEventListener: 브랜드를 찾을 수 없음 - ID: {}", product.getBrand().getId());
        }
        
        try {
            // 기존 ProductView 확인
            List<ProductView> existingViews = productQueryPort.findByProductId(product.getId());

            Category category = product.getCategory();
            ProductView productView = ProductView.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .brandId(product.getBrand().getId())
                    .brandName(brandName)
                    .categoryId(category.getId())
                    .categoryName(category.getName())
                    .categoryCode(category.getCode())
                    .price(product.getPrice())
                    .build();

            if (isUpdate && !existingViews.isEmpty()) {
                ProductView existingView = existingViews.get(0);
                productView = ProductView.builder()
                        .id(existingView.getId())
                        .productId(existingView.getProductId())
                        .productName(existingView.getProductName())
                        .brandId(product.getBrand().getId())
                        .brandName(brandName)
                        .categoryId(existingView.getCategoryId())
                        .categoryName(existingView.getCategoryName())
                        .categoryCode(existingView.getCategoryCode())
                        .price(product.getPrice())
                        .build();
                log.info("ProductEventListener: 기존 상품 뷰 업데이트 - ID: {}, 카테고리: {}", 
                        existingView.getId(), existingView.getCategoryCode());
            }
            
            ProductView savedView = productQueryPort.save(productView);
            log.info("ProductEventListener: 상품 뷰 저장 성공 - ID: {}, ProductID: {}, 카테고리: {}, 이벤트 타입: {}",
                    savedView.getId(), savedView.getProductId(), savedView.getCategoryCode(),
                    isUpdate ? "UPDATE" : "CREATE");
            
        } catch (DataIntegrityViolationException e) {
            log.error("ProductEventListener: 데이터 무결성 위반 오류 - ID: {}, 카테고리: {}, 오류: {}", 
                    product.getId(), product.getCategory().getCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("ProductEventListener: 상품 뷰 저장 중 오류 발생", e);
            throw e;
        }
    }
} 