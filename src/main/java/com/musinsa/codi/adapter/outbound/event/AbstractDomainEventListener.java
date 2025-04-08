package com.musinsa.codi.adapter.outbound.event;

import com.musinsa.codi.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class AbstractDomainEventListener<T extends DomainEvent> {
    
    @Transactional
    protected void handleEvent(T event) {
        try {
            log.info("이벤트 수신 - 타입: {}", event.getEventType());
            processEvent(event);
            log.info("이벤트 처리 완료 - 타입: {}", event.getEventType());
        } catch (Exception e) {
            log.error("이벤트 처리 중 오류 발생 - 타입: {}, 오류: {}", event.getEventType(), e.getMessage(), e);
            throw e;
        }
    }

    protected abstract void processEvent(T event);
} 