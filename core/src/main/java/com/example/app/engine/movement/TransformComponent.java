package com.example.app.engine.movement;

/**
 * Generic 2D transform (position + rotation).
 * Keep it non-contextual: no rendering/game rules here.
 */
public final class TransformComponent {
    public float x, y;
    public float rotationDeg;

    public TransformComponent(float x, float y) {
        this.x = x;
        this.y = y;
        this.rotationDeg = 0f;
    }
}
