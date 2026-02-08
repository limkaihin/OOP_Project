package com.example.app.engine.components;

/**
 * Marker interface for data-only components in the engine layer.
 * Keep components non-contextual (no game-specific rules here).
 */
public interface Component {

    /**
     * Optional hook for component self-validation.
     * Default is no-op so data-only components remain lightweight.
     */
    default void validate() { }
}

