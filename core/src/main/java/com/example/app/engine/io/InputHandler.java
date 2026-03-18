package com.example.app.engine.io;

import com.badlogic.gdx.Gdx;
import java.util.Map;

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

        // Keyboard
        for (Map.Entry<Integer, InputAction> e : bindings.viewBindings().entrySet()) {
            int keyCode = e.getKey();
            InputAction action = e.getValue();
            if (Gdx.input.isKeyJustPressed(keyCode)) {
                // Guarantee edge detection for fast keypresses
                state.set(action, true, false);
            } else {
                state.processInput(action, Gdx.input.isKeyPressed(keyCode));
            }
        }

        // Mouse
        for (Map.Entry<Integer, InputAction> e : bindings.viewMouseBindings().entrySet()) {
            int button = e.getKey();
            InputAction action = e.getValue();
            // Use isButtonJustPressed for mouse — catches fast clicks
            if (Gdx.input.isButtonJustPressed(button)) {
                // Manually set both current=true and previous=false to guarantee isJustPressed fires
                state.set(action, true, false);
            } else if (!Gdx.input.isButtonPressed(button)) {
                state.processInput(action, false);
            }
        }
    }

    public InputState getState() {
        return state;
    }

    // Optional hook for tests or platforms without polling.
    public void setKeyState(int keyCode, boolean isPressed) {
        InputAction action = bindings.getAction(keyCode);
        if (action != null) {
            state.processInput(action, isPressed);
        }
    }
}
