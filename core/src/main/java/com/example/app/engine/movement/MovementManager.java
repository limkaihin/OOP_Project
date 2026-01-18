package com.example.app.engine.movement;

import com.example.app.engine.entity.Entity;

/**
 * Movement management (engine-level).
 * Updates entity transforms based on velocity/acceleration components.
 */
public interface MovementManager {
    void update(float dt, Iterable<Entity> entities);
}
