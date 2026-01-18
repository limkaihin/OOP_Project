package com.example.app.engine.render;

/**
 * Non-contextual render description.
 */
public final class RenderableComponent {

    public enum RenderShape { RECT, CIRCLE }

    public RenderShape shape;

    // Color
    public float r=1f, g=1f, b=1f, a=1f;

    // RECT
    public float w=20f, h=20f;

    // CIRCLE
    public float radius=10f;

    public static RenderableComponent rect(float w, float h, float r, float g, float b, float a) {
        RenderableComponent rc = new RenderableComponent();
        rc.shape = RenderShape.RECT;
        rc.w = w; rc.h = h;
        rc.r = r; rc.g = g; rc.b = b; rc.a = a;
        return rc;
    }

    public static RenderableComponent circle(float radius, float r, float g, float b, float a) {
        RenderableComponent rc = new RenderableComponent();
        rc.shape = RenderShape.CIRCLE;
        rc.radius = radius;
        rc.r = r; rc.g = g; rc.b = b; rc.a = a;
        return rc;
    }
}