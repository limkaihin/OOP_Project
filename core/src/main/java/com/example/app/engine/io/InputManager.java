package com.example.app.engine.io;

/**
 * Updates an InputState from some platform source (libGDX, tests, replay files, etc.).
 */
public interface InputManager {
    void update(float dt);
    InputState getState();
}
