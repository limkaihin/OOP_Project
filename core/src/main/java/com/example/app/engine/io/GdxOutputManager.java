package com.example.app.engine.io;

import com.badlogic.gdx.Gdx;

/**
 * Simple OutputManager that logs through libGDX.
 */
public final class GdxOutputManager implements OutputManager {

    @Override
    public void log(String tag, String message) {
        Gdx.app.log(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable t) {
        Gdx.app.error(tag, message, t);
    }
}
