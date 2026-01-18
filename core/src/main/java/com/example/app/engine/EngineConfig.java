package com.example.app.engine;

public final class EngineConfig {
    public final int width;
    public final int height;
    public final String title;

    public final boolean vsync;
    public final int targetFps;

    // Engine timing (fixed-step)
    public final float fixedDt;
    public final int maxSubSteps;

    public EngineConfig(int width, int height, String title) {
        this(width, height, title, true, 60, 1f/60f, 5);
    }

    public EngineConfig(int width, int height, String title, boolean vsync, int targetFps, float fixedDt, int maxSubSteps) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.vsync = vsync;
        this.targetFps = targetFps;
        this.fixedDt = fixedDt;
        this.maxSubSteps = maxSubSteps;
    }
}