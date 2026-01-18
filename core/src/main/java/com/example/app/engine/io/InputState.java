package com.example.app.engine.io;

import java.util.EnumMap;

/**
 * Stores per-frame action state.
 * pressed: current frame state
 * justPressed: rising-edge (pressed now, not pressed last frame)
 */
public final class InputState {

    private final EnumMap<InputAction, Boolean> pressed = new EnumMap<>(InputAction.class);
    private final EnumMap<InputAction, Boolean> justPressed = new EnumMap<>(InputAction.class);

    public InputState() {
        for (InputAction a : InputAction.values()) {
            pressed.put(a, false);
            justPressed.put(a, false);
        }
    }

    public boolean isPressed(InputAction a) {
        return Boolean.TRUE.equals(pressed.get(a));
    }

    public boolean isJustPressed(InputAction a) {
        return Boolean.TRUE.equals(justPressed.get(a));
    }

    /**
     * Called by InputManager once per frame.
     */
    public void set(InputAction a, boolean isPressedNow, boolean wasPressedLastFrame) {
        pressed.put(a, isPressedNow);
        justPressed.put(a, isPressedNow && !wasPressedLastFrame);
    }
}
