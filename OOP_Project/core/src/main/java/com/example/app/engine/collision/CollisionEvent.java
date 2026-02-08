package com.example.app.engine.collision;

/**
 * Data object for a collision occurrence.
 * Java 8/11 friendly (no record).
 */
public final class CollisionEvent {
    private final CollisionPair pair;
    private final CollisionManifold manifold;

    public CollisionEvent(CollisionPair pair, CollisionManifold manifold) {
        if (pair == null) throw new IllegalArgumentException("pair cannot be null");
        if (manifold == null) throw new IllegalArgumentException("manifold cannot be null");
        this.pair = pair;
        this.manifold = manifold;
    }

    public CollisionPair getPair() {
        return pair;
    }

    public CollisionManifold getManifold() {
        return manifold;
    }
}
