package com.example.app.engine.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class InputBinding {
    private final Map<Integer, InputAction> keyToAction = new HashMap<>();
    //binds a key to an action
    public void bind(int keyCode, InputAction action) {
        if (action == null) return;
        keyToAction.put(keyCode, action);
    }
    //unbinds a key from an action
    public void unbind(int keyCode) {
        keyToAction.remove(keyCode);
    }
    //gets the action for a key
    public InputAction getAction(int keyCode) {
        return keyToAction.get(keyCode);
    }

    // Read-only view for debugging.
    public Map<Integer, InputAction> viewBindings() {
        return Collections.unmodifiableMap(keyToAction);
    }
    //clears all bindings
    public void clear() {
        keyToAction.clear();
    }
}
