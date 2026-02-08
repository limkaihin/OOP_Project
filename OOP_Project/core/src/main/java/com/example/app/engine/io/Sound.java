package com.example.app.engine.io;

import com.badlogic.gdx.Gdx;

/**
 * Lightweight sound wrapper (UML-aligned).
 *
 * If the file cannot be loaded (missing asset), calls become no-ops.
 */
public class Sound {
    private final String filePath;
    private float volume = 1f;
    private boolean playing = false;

    private com.badlogic.gdx.audio.Sound gdxSound;

    public Sound(String filePath) {
        this.filePath = filePath;
        try {
            if (filePath != null) {
                this.gdxSound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            }
        } catch (Throwable t) {
            // asset missing or audio init issue; keep as null
            this.gdxSound = null;
        }
    }

    public String getFilePath() { return filePath; }

    public void play() {
        if (gdxSound == null) return;
        gdxSound.play(volume);
        playing = true;
    }

    public void stop() {
        if (gdxSound == null) return;
        gdxSound.stop();
        playing = false;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
    }

    public float getVolume() { return volume; }

    public boolean isPlaying() { return playing; }

    public void dispose() {
        if (gdxSound != null) {
            gdxSound.dispose();
            gdxSound = null;
        }
    }
}
