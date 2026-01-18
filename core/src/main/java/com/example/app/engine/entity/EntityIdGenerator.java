package com.example.app.engine.entity;

import java.util.concurrent.atomic.AtomicInteger;

public final class EntityIdGenerator {
    private final AtomicInteger next = new AtomicInteger(1);

    public int nextId() {
        return next.getAndIncrement();
    }
}
