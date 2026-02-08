package com.example.app.engine.collision;

import com.example.app.engine.entity.Entity;

/**
 * Optional collision resolution interface.
 * Implementations can do separation, bounce, etc., without hardcoding simulation logic.
 */
public interface CollisionResolver {
    void resolve(Entity a, Entity b, CollisionManifold manifold);
}
