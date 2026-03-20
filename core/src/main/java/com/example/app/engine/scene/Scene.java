package com.example.app.engine.scene;

public interface Scene {
    // Allocate/prepare resources
    void onLoad();

    // Called when scene becomes the active scene
    void onEnter();

    // Called every frame while active
    void update(float dt);

    // Called when scene stops being active
    void onExit();

    // Free resources
    void onUnload();

    void render();

    void renderHud();

    // Scene name for debugging/logging
    default String name() {
        return getClass().getSimpleName();
    }
}