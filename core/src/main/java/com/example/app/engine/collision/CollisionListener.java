package com.example.app.engine.collision;

// Observer hook for collision interactions
public interface CollisionListener {
    void onCollision(CollisionEvent event);
}