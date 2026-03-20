package com.example.app.engine.entity;

import java.util.*;

public class EntityManager {
    private final EntityIdGenerator idGen = new EntityIdGenerator();
    private final List<Entity> entities = new ArrayList<>();
    private final Set<Integer> pendingDestroyIds = new HashSet<>();

    public EntityManager() {
    }

    public Entity create() {
        Entity e = new Entity(idGen.nextId());
        entities.add(e);
        return e;
    }

    public void destroy(Entity e) {
        if (e == null) {
            return;
        }
        // Add entity to destroy
        pendingDestroyIds.add(e.getId());
    }

    public void update(float dt) {
        // No entity to delete
        if (pendingDestroyIds.isEmpty()) {
            return;
        }

        // Remove entities
        for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
            Entity e = it.next();
            if (pendingDestroyIds.contains(e.getId()))
                it.remove();
        }
        pendingDestroyIds.clear();
    }

    public List<Entity> getAll() {
        return Collections.unmodifiableList(entities);
    }

    public Entity getEntity(int id) {
        for (Entity e : entities) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}