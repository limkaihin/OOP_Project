package com.example.app.engine.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

<<<<<<< HEAD

=======
>>>>>>> c94a7c550fcfd4d8063dd6edbe7a0eb0a87dbfb7
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

<<<<<<< HEAD
    // Read-only view for debugging.
=======
    // Read-only view for debugging
>>>>>>> c94a7c550fcfd4d8063dd6edbe7a0eb0a87dbfb7
    public Map<Integer, InputAction> viewBindings() {
        return Collections.unmodifiableMap(keyToAction);
    }
    //clears all bindings
    public void clear() {
        keyToAction.clear();
    }
}
