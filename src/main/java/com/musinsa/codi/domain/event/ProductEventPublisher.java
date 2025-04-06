package com.musinsa.codi.domain.event;

public interface ProductEventPublisher {
    void publish(ProductEvent event);
} 