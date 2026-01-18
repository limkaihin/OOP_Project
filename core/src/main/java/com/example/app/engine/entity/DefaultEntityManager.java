package com.example.app.engine.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Simple entity manager suitable for Part 1 demos.
 * Keeps entities in a list and supports deferred destroy.
 */
public final class DefaultEntityManager implements EntityManager {

    private final EntityIdGenerator idGen = new EntityIdGenerator();
    private final List<Entity> entities = new ArrayList<>();
    private final List<Integer> pendingDestroyIds = new ArrayList<>();

    @Override
    public Entity create() {
        Entity e = new Entity(idGen.nextId());
        entities.add(e);
        return e;
    }

    @Override
    public void destroy(Entity e) {
        if (e == null) return;
        pendingDestroyIds.add(e.getId());
    }

    @Override
    public void update(float dt) {
        if (pendingDestroyIds.isEmpty()) return;

        for (Iterator<Entity> it = entities.iterator(); it.hasNext(); ) {
            Entity e = it.next();
            if (pendingDestroyIds.contains(e.getId())) {
                it.remove();
            }
        }
        pendingDestroyIds.clear();
    }

    @Override
    public List<Entity> getAll() {
        return Collections.unmodifiableList(entities);
    }
}
