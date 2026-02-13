package com.example.app.engine.movement;
import com.example.app.engine.components.Component;
/**
 * Optional acceleration component for smoother movement.
 */
public final class AccelerationComponent implements Component {
    public float ax, ay;

    public AccelerationComponent(float ax, float ay) {
        this.ax = ax;
        this.ay = ay;
    }
}
