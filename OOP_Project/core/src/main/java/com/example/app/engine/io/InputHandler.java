package com.example.app.engine.io;

import com.badlogic.gdx.Gdx;
import java.util.Map;

<<<<<<< HEAD

=======
>>>>>>> c94a7c550fcfd4d8063dd6edbe7a0eb0a87dbfb7
public class InputHandler {

    private final InputBinding bindings;
    private final InputState state = new InputState();
    //to split up the InputHandler into input status checking and input binding
    public InputHandler(InputBinding bindings) {
        this.bindings = (bindings == null) ? new InputBinding() : bindings;
    }

    public void update(float deltaTime) {
        // Edge-detection history at start of the frame
        state.nextFrame();

        // Process all bound keys
        for (Map.Entry<Integer, InputAction> e : bindings.viewBindings().entrySet()) {
            int keyCode = e.getKey();
            InputAction action = e.getValue();
            boolean down = Gdx.input.isKeyPressed(keyCode);
            // Multiple keys may map to the same action
            if (down) {
                state.processInput(action, true);
            }
        }

        // Ensure actions with no active bound key are false, we only ever set true above
        for (InputAction a : InputAction.values()) {
            boolean anyDown = isAnyKeyDownForAction(a);
            state.processInput(a, anyDown);
        }
    }

    private boolean isAnyKeyDownForAction(InputAction action) {
        for (Map.Entry<Integer, InputAction> e : bindings.viewBindings().entrySet()) {
            if (e.getValue() == action && Gdx.input.isKeyPressed(e.getKey())) return true;
        }
        return false;
    }

    public InputState getState() {
        return state;
    }

<<<<<<< HEAD
    // Optional hook for tests or platforms without polling.
=======
    // Optional hook for tests or platforms without polling
>>>>>>> c94a7c550fcfd4d8063dd6edbe7a0eb0a87dbfb7
    public void setKeyState(int keyCode, boolean isPressed) {
        InputAction action = bindings.getAction(keyCode);
        if (action != null) {
            state.processInput(action, isPressed);
        }
    }
}
