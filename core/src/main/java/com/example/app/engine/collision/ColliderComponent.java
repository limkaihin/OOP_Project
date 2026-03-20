package com.example.app.engine.collision;

import com.example.app.engine.components.Component;

public final class ColliderComponent implements Component {

    public enum ColShapeType {
        CIRCLE, AABB
    }

    // Collision filtering
    public int layer = 1; // In layer
    public int mask = ~0; // Collide with layers"
    public boolean isTrigger = false;

    // Offset from transform position
    public float offsetX = 0f;
    public float offsetY = 0f;

    // Shape data
    public final ColShapeType type;

    // Circle
    public float radius;

    // Half-extents
    public float halfWidth;
    public float halfHeight;

    private ColliderComponent(ColShapeType type) {
        this.type = type;
    }

    public static ColliderComponent circle(float radius) {
        ColliderComponent c = new ColliderComponent(ColShapeType.CIRCLE);
        c.radius = radius;
        return c;
    }

    public static ColliderComponent aabb(float halfWidth, float halfHeight) {
        ColliderComponent c = new ColliderComponent(ColShapeType.AABB);
        c.halfWidth = halfWidth;
        c.halfHeight = halfHeight;
        return c;
    }
}
