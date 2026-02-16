package com.example.app.engine.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InputBinding {
    private final Map<Integer, InputAction> keyToAction = new HashMap<>();

    public void bind(int keyCode, InputAction action) {
        if (action == null) return;
        keyToAction.put(keyCode, action);
    }

    public void unbind(int keyCode) {
        keyToAction.remove(keyCode);
    }

    public InputAction getAction(int keyCode) {
        return keyToAction.get(keyCode);
    }

    // Read-only view for debugging
    public Map<Integer, InputAction> viewBindings() {
        return Collections.unmodifiableMap(keyToAction);
    }

    public void clear() {
        keyToAction.clear();
    }
}
