package com.example.app.engine.components;

/**
 * Minimal abstraction for "things that own components".
 * This lets managers/systems depend on an interface (SOLID) instead of a concrete Entity class.
 */
public interface ComponentAccess {
    <T> void addComponent(Class<T> type, T component);
    <T> T getComponent(Class<T> type);
    <T> boolean hasComponent(Class<T> type);
    <T> void removeComponent(Class<T> type);
}
