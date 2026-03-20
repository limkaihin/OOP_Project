package com.example.app.engine.util;

// Helper to do null/argument checks
public final class Preconditions {
    private Preconditions() {
    }

    public static <T> T notNull(T value, String name) {
        if (value == null)
            throw new IllegalArgumentException(name + " cannot be null");
        return value;
    }

    public static void check(boolean condition, String message) {
        if (!condition)
            throw new IllegalArgumentException(message);
    }
}