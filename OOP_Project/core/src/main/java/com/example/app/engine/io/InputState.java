package com.example.app.engine.io;

import java.util.EnumMap;
import java.util.Map;

<<<<<<< HEAD
// Tracks input action state across frames (UML-aligned).
// - currentState: state for this frame
// - previousState: state for previous frame
=======
>>>>>>> c94a7c550fcfd4d8063dd6edbe7a0eb0a87dbfb7
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
<<<<<<< HEAD
    //updates an action's state for this frame
=======

    // Update an action's state for this frame
>>>>>>> c94a7c550fcfd4d8063dd6edbe7a0eb0a87dbfb7
    public void processInput(InputAction action, boolean isDown) {
        if (action == null) return;
        currentState.put(action, isDown);
    }
<<<<<<< HEAD
    //advances the frame
=======

    // previousState <- currentState, called once per frame after all input has been processed
>>>>>>> c94a7c550fcfd4d8063dd6edbe7a0eb0a87dbfb7
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
<<<<<<< HEAD
    //backward-compatible method used by older input implementations
=======

    // Backward-compatible method used by older input implementations
>>>>>>> c94a7c550fcfd4d8063dd6edbe7a0eb0a87dbfb7
    public void set(InputAction a, boolean isPressedNow, boolean wasPressedLastFrame) {
        currentState.put(a, isPressedNow);
        previousState.put(a, wasPressedLastFrame);
    }
}
