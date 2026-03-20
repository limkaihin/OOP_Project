package com.example.app.engine.collision;

import com.example.app.engine.entity.Entity;
import java.util.List;

public interface CollisionManager {
    /*--------------------------------------------------------------------------
    Runs collision detection for the current frame
    Returns detected collisions
    --------------------------------------------------------------------------*/
    List<CollisionEvent> update(float dt, Iterable<Entity> entities);
}