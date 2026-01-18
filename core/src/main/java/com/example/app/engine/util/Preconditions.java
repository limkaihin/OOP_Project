package com.example.app.engine.util;

/**
 * Tiny helper to keep null/argument checks consistent across the engine.
 */
public final class Preconditions {
    private Preconditions() { }

    public static <T> T notNull(T value, String name) {
        if (value == null) throw new IllegalArgumentException(name + " cannot be null");
        return value;
    }

    public static void check(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
}
