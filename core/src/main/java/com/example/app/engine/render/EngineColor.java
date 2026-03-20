package com.example.app.engine.render;

public final class EngineColor {
    public final float r, g, b, a;

    public EngineColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    // Common presets
    public static final EngineColor WHITE = new EngineColor(1f, 1f, 1f, 1f);
    public static final EngineColor BLACK = new EngineColor(0f, 0f, 0f, 1f);
    public static final EngineColor RED = new EngineColor(1f, 0f, 0f, 1f);
    public static final EngineColor GREEN = new EngineColor(0f, 1f, 0f, 1f);
    public static final EngineColor BLUE = new EngineColor(0f, 0f, 1f, 1f);
    public static final EngineColor YELLOW = new EngineColor(1f, 1f, 0f, 1f);
    public static final EngineColor LIGHT_GRAY = new EngineColor(0.75f, 0.75f, 0.75f, 1f);
    public static final EngineColor CLEAR = new EngineColor(0f, 0f, 0f, 0f);
}