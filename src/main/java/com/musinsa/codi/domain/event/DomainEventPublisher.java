package com.musinsa.codi.domain.event;

public interface DomainEventPublisher<T extends DomainEvent> {
    void publish(T event);
} 