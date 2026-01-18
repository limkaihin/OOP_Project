package com.example.app.engine.util;

/**
 * Minimal float math helpers (avoid extra dependencies).
 */
public final class Mathf {
    private Mathf() { }

    public static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    public static float length(float x, float y) {
        return (float) Math.sqrt(x * x + y * y);
    }

    public static float length2(float x, float y) {
        return x * x + y * y;
    }

    public static float distance(float ax, float ay, float bx, float by) {
        return length(bx - ax, by - ay);
    }

    public static float distance2(float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        return dx * dx + dy * dy;
    }
}
