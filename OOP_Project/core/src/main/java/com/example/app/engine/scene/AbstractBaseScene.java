package com.example.app.engine.scene;

/**
 * Abstract base scene (UML-aligned).
 * Scenes can override lifecycle methods as needed.
 */
public abstract class AbstractBaseScene implements Scene {
    @Override public void onLoad() { }
    @Override public void onEnter() { }
    @Override public void onExit() { }
    @Override public void onUnload() { }
}
