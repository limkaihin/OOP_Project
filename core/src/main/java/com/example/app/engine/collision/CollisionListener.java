package com.example.app.engine.collision;

/**
 * Observer hook for collision interactions.
 * Keep it generic: demo/game logic decides meaning.
 */
public interface CollisionListener {
    void onCollision(CollisionEvent event);
}
