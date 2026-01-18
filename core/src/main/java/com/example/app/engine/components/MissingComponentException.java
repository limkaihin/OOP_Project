package com.example.app.engine.components;

public final class MissingComponentException extends RuntimeException {
    public MissingComponentException(String message) {
        super(message);
    }
}
