package com.example.app.engine.collision;

/**
 * Minimal collision manifold.
 * normal points from A -> B, penetration is positive for overlap.
 */
public final class CollisionManifold {
    public final float normalX;
    public final float normalY;
    public final float penetration;

    public CollisionManifold(float normalX, float normalY, float penetration) {
        this.normalX = normalX;
        this.normalY = normalY;
        this.penetration = penetration;
    }
}
