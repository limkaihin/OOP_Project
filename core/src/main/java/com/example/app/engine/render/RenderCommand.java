package com.example.app.engine.render;

public final class RenderCommand {

    public enum ShapeType { RECT, CIRCLE, FULLSCREEN_FADE }

    public final ShapeType type;

    // Common
    public final float r, g, b, a;

    // Rect
    public final float x, y, w, h;

    // Circle
    public final float cx, cy, radius;

    private RenderCommand(ShapeType type,
                          float r, float g, float b, float a,
                          float x, float y, float w, float h,
                          float cx, float cy, float radius) {
        this.type = type;
        this.r = r; this.g = g; this.b = b; this.a = a;
        this.x = x; this.y = y; this.w = w; this.h = h;
        this.cx = cx; this.cy = cy; this.radius = radius;
    }

    public static RenderCommand rect(float x, float y, float w, float h, float r, float g, float b, float a) {
        return new RenderCommand(ShapeType.RECT, r,g,b,a, x,y,w,h, 0,0,0);
    }

    public static RenderCommand circle(float cx, float cy, float radius, float r, float g, float b, float a) {
        return new RenderCommand(ShapeType.CIRCLE, r,g,b,a, 0,0,0,0, cx,cy,radius);
    }

    public static RenderCommand fullscreenFade(float r, float g, float b, float a) {
        return new RenderCommand(ShapeType.FULLSCREEN_FADE, r,g,b,a, 0,0,0,0, 0,0,0);
    }
}