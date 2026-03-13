package com.example.app.engine.io;

import java.util.EnumMap;
import java.util.Map;

// Tracks input action state across frames (UML-aligned).
// - currentState: state for this frame
// - previousState: state for previous frame
public final class InputState {

    private final EnumMap<InputAction, Boolean> currentState = new EnumMap<>(InputAction.class);
    private final EnumMap<InputAction, Boolean> previousState = new EnumMap<>(InputAction.class);
    //initializes the input state
    public InputState() {
        for (InputAction a : InputAction.values()) {
            currentState.put(a, false);
            previousState.put(a, false);
        }
    }
    //updates an action's state for this frame
    public void processInput(InputAction action, boolean isDown) {
        if (action == null) return;
        currentState.put(action, isDown);
    }
    //advances the frame
    public void nextFrame() {
        for (Map.Entry<InputAction, Boolean> e : currentState.entrySet()) {
            previousState.put(e.getKey(), e.getValue());
        }
    }
    //returns if an action is pressed
    public boolean isPressed(InputAction a) {
        return Boolean.TRUE.equals(currentState.get(a));
    }
    //returns if an action is just pressed
    public boolean isJustPressed(InputAction a) {
        boolean now = Boolean.TRUE.equals(currentState.get(a));
        boolean prev = Boolean.TRUE.equals(previousState.get(a));
        return now && !prev;
    }
    //backward-compatible method used by older input implementations
    public void set(InputAction a, boolean isPressedNow, boolean wasPressedLastFrame) {
        currentState.put(a, isPressedNow);
        previousState.put(a, wasPressedLastFrame);
    }
}
