package com.example.app.engine.collision;

import com.example.app.engine.entity.Entity;

public interface CollisionResolver {
    void resolve(Entity a, Entity b, CollisionManifold manifold);
}