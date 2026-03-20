package com.example.app.engine.io;

import com.badlogic.gdx.Gdx;

// If file cannot be loaded, calls become no-ops
public class Sound {
    private final String filePath;
    private float volume = 1f;
    private boolean playing = false;
    // Track playing sound instance
    private long soundId = -1;

    private com.badlogic.gdx.audio.Sound gdxSound;

    // Initializes sound
    public Sound(String filePath) {
        this.filePath = filePath;
        try {
            if (filePath != null) {
                this.gdxSound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            }
        } catch (Throwable t) {
            // Asset missing or audio init issue; keep as null
            this.gdxSound = null;
        }
    }

    // Get the file path
    public String getFilePath() {
        return filePath;
    }

    // Plays sound
    public void play() {
        if (gdxSound == null)
            return;
        if (playing) {
            // Stop previous instance if playing
            stop();
        }
        soundId = gdxSound.play(volume);
        playing = true;
    }

    // Stop sound
    public void stop() {
        if (gdxSound == null || soundId == -1)
            return;
        gdxSound.stop(soundId);
        playing = false;
    }

    // Set volume
    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
        // Update volume if currently playing
        if (gdxSound != null && soundId != -1) {
            gdxSound.setVolume(soundId, this.volume);
        }
    }

    // Get volume
    public float getVolume() {
        return volume;
    }

    // Check if sound is playing
    public boolean isPlaying() {
        return playing && soundId != -1;
    }

    public void dispose() {
        stop();
        if (gdxSound != null) {
            gdxSound.dispose();
            gdxSound = null;
        }
    }
}