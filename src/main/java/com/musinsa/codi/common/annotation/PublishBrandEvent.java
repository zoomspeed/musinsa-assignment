package com.musinsa.codi.common.annotation;

import com.musinsa.codi.domain.event.BrandEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishBrandEvent {
    BrandEventType eventType();
} 