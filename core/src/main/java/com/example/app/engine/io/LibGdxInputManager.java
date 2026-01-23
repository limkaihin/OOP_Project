package com.example.app.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.HashMap;
import java.util.Map;

public final class LibGdxInputManager implements InputManager {

    private final InputBindings bindings;
    private final InputState state = new InputState();

    // Track last-frame pressed state per action for edge detection
    private final Map<InputAction, Boolean> lastPressed = new HashMap<InputAction, Boolean>();

    public LibGdxInputManager(InputBindings bindings) {
        this.bindings = (bindings == null) ? new InputBindings() : bindings;

        for (InputAction a : InputAction.values()) lastPressed.put(a, false);
    }

    @Override
    public void update(float dt) {
        for (InputAction action : InputAction.values()) {
            boolean nowPressed = isAnyKeyBoundPressed(action);
            boolean wasPressed = Boolean.TRUE.equals(lastPressed.get(action));

            state.set(action, nowPressed, wasPressed);
            lastPressed.put(action, nowPressed);
        }
    }

    private boolean isAnyKeyBoundPressed(InputAction action) {
        for (Map.Entry<Integer, InputAction> entry : bindings.viewBindings().entrySet()) {
            if (entry.getValue() == action) {
                int keycode = entry.getKey().intValue();
                if (Gdx.input.isKeyPressed(keycode)) return true; // libGDX polling [web:95]
            }
        }
        return false;
    }

    @Override
    public InputState getState() {
        return state;
    }
}
