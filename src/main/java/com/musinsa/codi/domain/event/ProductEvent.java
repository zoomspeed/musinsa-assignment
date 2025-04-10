package com.musinsa.codi.domain.event;

import com.musinsa.codi.domain.model.command.Product;
import lombok.Getter;

@Getter
public class ProductEvent implements DomainEvent {
    private final Product product;
    private final ProductEventType eventType;

    public ProductEvent(Product product, ProductEventType eventType) {
        this.product = product;
        this.eventType = eventType;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }
} 