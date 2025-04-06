package com.musinsa.codi.common.aop;

import com.musinsa.codi.common.annotation.PublishProductEvent;
import com.musinsa.codi.domain.event.ProductEvent;
import com.musinsa.codi.domain.event.ProductEventPublisher;
import com.musinsa.codi.domain.model.command.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ProductEventAspect {
    private final ProductEventPublisher productEventPublisher;

    @Around("@annotation(com.musinsa.codi.common.annotation.PublishProductEvent)")
    public Object publishEventAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("ProductEventAspect: 메서드 실행 시작 - {}", joinPoint.getSignature().getName());
        
        // 메서드 실행
        Object result = joinPoint.proceed();
        
        // 메서드 시그니처와 어노테이션 정보 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PublishProductEvent annotation = method.getAnnotation(PublishProductEvent.class);
        
        log.info("ProductEventAspect: 메서드 반환 타입 - {}, 이벤트 타입 - {}",
                method.getReturnType().getName(), annotation.eventType());
        
        // 반환 타입에 따라 다르게 처리
        if (result instanceof Product product) {
            // Product를 반환하는 메서드인 경우
            log.info("ProductEventAspect: Product 반환 - ID: {}, 이름: {}",
                    product.getId(), product.getName());
            productEventPublisher.publish(new ProductEvent(product, annotation.eventType()));
        } else if (method.getReturnType() == void.class) {
            // void 메서드인 경우, 첫 번째 인자가 Product인지 확인
            Object[] args = joinPoint.getArgs();
            log.info("ProductEventAspect: void 메서드, 인자 개수: {}", args.length);
            
            boolean foundProduct = false;
            // 적절한 인자를 찾아 이벤트 발행
            for (Object arg : args) {
                if (arg instanceof Product product) {
                    log.info("ProductEventAspect: void 메서드의 Product 인자 - ID: {}, 이름: {}",
                            product.getId(), product.getName());
                    productEventPublisher.publish(new ProductEvent(product, annotation.eventType()));
                    foundProduct = true;
                    break;
                }
            }
            
            if (!foundProduct) {
                log.warn("ProductEventAspect: Product 인자를 찾을 수 없음 - {}", joinPoint.getSignature().getName());
            }
        } else {
            log.warn("ProductEventAspect: 처리할 수 없는 반환 타입 - {}", method.getReturnType().getName());
        }
        
        return result;
    }
} 