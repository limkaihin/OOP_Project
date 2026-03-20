package com.example.app.engine.scene;

public interface Scene {
    // Allocate/prepare resources (called once when first loaded)
    void onLoad();

    // Called when scene becomes the active scene
    void onEnter();

    // Called every frame while active
    void update(float dt);

    // Called when scene stops being active (before another becomes current)
    void onExit();

    // Free resources (called once when permanently removed/unloaded)
    void onUnload();

    void render();

    void renderHud();

    // Convenience: scene name for debugging/logging
    default String name() {
        return getClass().getSimpleName();
    }
}