package com.musinsa.codi.common.aop;

import com.musinsa.codi.common.annotation.PublishBrandEvent;
import com.musinsa.codi.domain.event.BrandEvent;
import com.musinsa.codi.domain.event.BrandEventPublisher;
import com.musinsa.codi.domain.model.command.Brand;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class BrandEventAspect {
    private final BrandEventPublisher brandEventPublisher;

    @AfterReturning(
            pointcut = "@annotation(publishBrandEvent)",
            returning = "brand"
    )
    public void publishEvent(PublishBrandEvent publishBrandEvent, Brand brand) {
        brandEventPublisher.publish(new BrandEvent(brand, publishBrandEvent.eventType()));
    }
} 