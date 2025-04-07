package com.musinsa.codi.adapter.outbound.event;

import com.musinsa.codi.domain.event.ProductEvent;
import com.musinsa.codi.domain.event.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventPublisherImpl implements ProductEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(ProductEvent event) {
        eventPublisher.publishEvent(event);
    }
} 