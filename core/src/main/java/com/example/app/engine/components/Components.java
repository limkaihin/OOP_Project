package com.example.app.engine.components;

/**
 * Small helper utilities for safer component access.
 */
public final class Components {
    private Components() { }

    public static <T> T require(ComponentAccess owner, Class<T> type) {
        T c = owner.getComponent(type);
        if (c == null) {
            throw new MissingComponentException("Missing required component: " + type.getSimpleName());
        }
        return c;
    }
}
