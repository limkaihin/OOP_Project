package com.example.app.engine.util;

/**
 * Generic engine exception (non-contextual).
 */
public class EngineException extends RuntimeException {
    public EngineException(String message) { super(message); }
    public EngineException(String message, Throwable cause) { super(message, cause); }
}
