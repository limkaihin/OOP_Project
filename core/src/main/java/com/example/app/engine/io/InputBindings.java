package com.example.app.engine.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps platform-specific keycodes to engine-level InputAction.
 * Keeps the engine independent from "WASD" assumptions (demo decides bindings).
 */
public final class InputBindings {

    private final Map<Integer, InputAction> keyToAction = new HashMap<>();

    public void bind(int keycode, InputAction action) {
        if (action == null) throw new IllegalArgumentException("action cannot be null");
        keyToAction.put(keycode, action);
    }

    public void unbind(int keycode) {
        keyToAction.remove(keycode);
    }

    public InputAction getAction(int keycode) {
        return keyToAction.get(keycode);
    }

    public Map<Integer, InputAction> viewBindings() {
        return Collections.unmodifiableMap(keyToAction);
    }
}
