package com.example.app.engine.scene;

/**
 * Scene management API.
 * Implementations can be stack-based (push/pop) or single-scene (replace only).
 */
public interface SceneManager {
    void push(Scene scene);
    void pop();
    void switchTo(Scene scene);
    Scene current();
    int size();
}
