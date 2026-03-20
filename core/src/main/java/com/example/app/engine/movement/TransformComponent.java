package com.example.app.engine.movement;

import com.example.app.engine.components.Component;

public final class TransformComponent implements Component {
    public float x, y;
    public float rotationDeg;

    public TransformComponent(float x, float y) {
        this.x = x;
        this.y = y;
        this.rotationDeg = 0f;
    }
}