package com.example.app.engine.scene;

/**
 * Convenience base class so scenes only override what they need.
 */
public abstract class BaseScene implements Scene {
    @Override public void onLoad() { }
    @Override public void onEnter() { }
    @Override public void onExit() { }
    @Override public void onUnload() { }
}
