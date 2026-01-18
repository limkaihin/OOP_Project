package com.example.app.engine.movement;

/**
 * Optional acceleration component for smoother movement.
 */
public final class AccelerationComponent {
    public float ax, ay;

    public AccelerationComponent(float ax, float ay) {
        this.ax = ax;
        this.ay = ay;
    }
}
