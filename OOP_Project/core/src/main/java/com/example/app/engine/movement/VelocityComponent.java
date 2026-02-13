package com.example.app.engine.movement;
import com.example.app.engine.components.Component;
/**
 * Generic 2D velocity.
 */
public final class VelocityComponent implements Component {
    public float vx, vy;

    public VelocityComponent(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }
}
