package com.example.app.engine.collision;

import com.example.app.engine.entity.Entity;

/**
 * Two entities involved in a collision (order not guaranteed).
 * Java 8/11 friendly (no record).
 */
public final class CollisionPair {
    private final Entity a;
    private final Entity b;

    public CollisionPair(Entity a, Entity b) {
        if (a == null) throw new IllegalArgumentException("a cannot be null");
        if (b == null) throw new IllegalArgumentException("b cannot be null");
        this.a = a;
        this.b = b;
    }

    public Entity getA() {
        return a;
    }

    public Entity getB() {
        return b;
    }
}
