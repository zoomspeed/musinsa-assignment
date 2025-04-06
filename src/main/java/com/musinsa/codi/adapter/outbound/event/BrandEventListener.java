package com.musinsa.codi.adapter.outbound.event;

import com.musinsa.codi.domain.event.BrandEvent;
import com.musinsa.codi.domain.event.BrandEventType;
import com.musinsa.codi.domain.model.query.BrandView;
import com.musinsa.codi.domain.port.query.BrandQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BrandEventListener {
    private final BrandQueryPort brandQueryPort;

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
            }
            case DELETED -> brandQueryPort.delete(event.getBrand().getId());
        }
    }
} 