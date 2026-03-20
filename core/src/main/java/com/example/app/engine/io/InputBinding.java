package com.example.app.engine.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InputBinding {
    private final Map<Integer, InputAction> keyToAction = new HashMap<>();
    private final Map<Integer, InputAction> mouseToAction = new HashMap<>();

    // Binds a key to an action
    public void bind(int keyCode, InputAction action) {
        if (action == null)
            return;
        keyToAction.put(keyCode, action);
    }

    // Unbinds a key from an action
    public void unbind(int keyCode) {
        keyToAction.remove(keyCode);
    }

    public void bindMouse(int button, InputAction action) {
        if (action == null)
            return;
        mouseToAction.put(button, action);
    }

    public Map<Integer, InputAction> viewMouseBindings() {
        return Collections.unmodifiableMap(mouseToAction);
    }

    // Gets action for a key
    public InputAction getAction(int keyCode) {
        return keyToAction.get(keyCode);
    }

    // Read-only view for debugging.
    public Map<Integer, InputAction> viewBindings() {
        return Collections.unmodifiableMap(keyToAction);
    }

    // Clear all bindings
    public void clear() {
        keyToAction.clear();
    }
}