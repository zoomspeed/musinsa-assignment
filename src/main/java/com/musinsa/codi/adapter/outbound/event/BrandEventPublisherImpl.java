package com.musinsa.codi.adapter.outbound.event;

import com.musinsa.codi.domain.event.BrandEvent;
import com.musinsa.codi.domain.event.BrandEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrandEventPublisherImpl implements BrandEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(BrandEvent event) {
        eventPublisher.publishEvent(event);
    }
} 