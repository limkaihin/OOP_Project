package com.example.app.engine.entity;

import java.util.List;

public interface EntityManager {
    Entity create();
    void destroy(Entity e);

    /**
     * Optional place for deferred deletions, housekeeping, etc.
     */
    void update(float dt);

    /**
     * Snapshot list for systems/managers to iterate safely.
     */
    List<Entity> getAll();
}
