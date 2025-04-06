package com.musinsa.codi.domain.event;

import com.musinsa.codi.domain.model.command.Brand;
import lombok.Getter;

@Getter
public class BrandEvent {
    private final Brand brand;
    private final BrandEventType eventType;

    public BrandEvent(Brand brand, BrandEventType eventType) {
        this.brand = brand;
        this.eventType = eventType;
    }
} 