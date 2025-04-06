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
        
        if (event.getEventType() == ProductEventType.DELETED) {
            // 상품이 삭제된 경우, 해당 상품 뷰도 삭제
            log.info("ProductEventListener: 상품 뷰 삭제 - ID: {}", product.getId());
            // ID로 ProductView를 찾아서 삭제
            List<ProductView> productViews = productQueryPort.findByProductId(product.getId());
            for (ProductView productView : productViews) {
                productQueryPort.deleteByViewId(productView.getViewId());
            }
            return;
        }
        
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
            // 기존에 같은 ID와 카테고리 조합이 있는지 확인
            Optional<ProductView> existingViewOpt = productQueryPort.findByProductIdAndCategory(
                    product.getId(), product.getCategory());
            
            // 기존에 같은 ID와 카테고리 조합이 있으면 먼저 삭제
            if (existingViewOpt.isPresent()) {
                ProductView existingView = existingViewOpt.get();
                log.info("ProductEventListener: 기존 상품 뷰 삭제 - ID: {}, 카테고리: {}, ViewID: {}", 
                        existingView.getId(), existingView.getCategory(), existingView.getViewId());
                productQueryPort.deleteByViewId(existingView.getViewId());
            }
            
            // 새 ProductView 생성
            ProductView productView = ProductView.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .category(product.getCategory())
                    .price(product.getPrice())
                    .brandId(product.getBrand().getId())
                    .brandName(brandName)
                    .build();
            
            ProductView savedView = productQueryPort.save(productView);
            log.info("ProductEventListener: 상품 뷰 저장 성공 - ID: {}, 이름: {}, 카테고리: {}",
                    savedView.getId(), savedView.getName(), savedView.getCategory());
        } catch (DataIntegrityViolationException e) {
            log.error("ProductEventListener: 데이터 무결성 위반 오류 - ID: {}, 카테고리: {}, 오류: {}", 
                    product.getId(), product.getCategory(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("ProductEventListener: 상품 뷰 저장 중 오류 발생", e);
            throw e;
        }
    }
} 