package com.example.app.engine.entity;

import com.example.app.engine.components.ComponentAccess;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Non-contextual Entity: just an ID + a bag of components.
 * No simulation-specific logic should live here.
 */
public final class Entity implements ComponentAccess {
    private final int id;
    private final Map<Class<?>, Object> components = new HashMap<>();

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Useful for debugging / tooling (read-only view).
     */
    public Map<Class<?>, Object> viewComponents() {
        return Collections.unmodifiableMap(components);
    }

    @Override
    public <T> void addComponent(Class<T> type, T component) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (component == null) throw new IllegalArgumentException("component cannot be null");
        components.put(type, component);
    }

    @Override
    public <T> T getComponent(Class<T> type) {
        Object c = components.get(type);
        return (c == null) ? null : type.cast(c);
    }

    @Override
    public <T> boolean hasComponent(Class<T> type) {
        return components.containsKey(type);
    }

    @Override
    public <T> void removeComponent(Class<T> type) {
        components.remove(type);
    }

    @Override
    public String toString() {
        return "Entity{id=" + id + ", components=" + components.size() + "}";
    }
}
