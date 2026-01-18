package com.example.app.engine.io;

/**
 * Generic output/log interface.
 * Demo can implement UI overlays, file logging, etc.
 */
public interface OutputManager {
    void log(String tag, String message);
    void error(String tag, String message, Throwable t);
}
