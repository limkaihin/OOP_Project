package com.example.app.engine.collision;

/**
 * Generic collider component (non-contextual).
 * Supports Circle and AABB shapes.
 */
public final class ColliderComponent {

    public enum ShapeType { CIRCLE, AABB }

    // Collision filtering (generic)
    public int layer = 1;          // "I am in these layers"
    public int mask = ~0;          // "I collide with these layers"
    public boolean isTrigger = false;

    // Offset from transform position (optional)
    public float offsetX = 0f;
    public float offsetY = 0f;

    // Shape data
    public final ShapeType type;

    // Circle
    public float radius;

    // AABB (half extents)
    public float halfWidth;
    public float halfHeight;

    private ColliderComponent(ShapeType type) {
        this.type = type;
    }

    public static ColliderComponent circle(float radius) {
        ColliderComponent c = new ColliderComponent(ShapeType.CIRCLE);
        c.radius = radius;
        return c;
    }

    public static ColliderComponent aabb(float halfWidth, float halfHeight) {
        ColliderComponent c = new ColliderComponent(ShapeType.AABB);
        c.halfWidth = halfWidth;
        c.halfHeight = halfHeight;
        return c;
    }
}
