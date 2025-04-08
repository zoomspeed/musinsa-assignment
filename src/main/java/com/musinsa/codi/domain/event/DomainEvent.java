package com.musinsa.codi.domain.event;

public interface DomainEvent {
    EventType getEventType();
}

interface EventType {} 