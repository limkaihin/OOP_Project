package com.example.app.engine.io;

/**
 * Simple audio manager (UML-aligned).
 * Uses {@link Sound} as a minimal wrapper.
 */
public class AudioPlayer {

    private float masterVolume = 1f;
    private Sound currentMusic;

    public AudioPlayer() {
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float masterVolume) {
        this.masterVolume = Math.max(0f, Math.min(1f, masterVolume));
        if (currentMusic != null)
            currentMusic.setVolume(this.masterVolume);
    }

    public void playMusic(String filePath) {
        stopMusic();

        // Create and play the sound
        currentMusic = new Sound(filePath);
        if (currentMusic != null) {
            currentMusic.setVolume(masterVolume);
            currentMusic.play();
        }
    }

    // Also add this convenience method
    public void playSound(String filePath) {
        Sound sound = new Sound(filePath);
        if (sound != null) {
            sound.setVolume(masterVolume);
            sound.play();
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    public void addSoundPath(String path) {
        // Optional extension point; no-op for now.
    }

    public boolean hasSound(String soundId) {
        return false;
    }

    public Sound getCurrentMusic() {
        return currentMusic;
    }

    public void pauseAll() {
        // Not supported in this minimal wrapper.
    }

    public void resumeAll() {
        // Not supported in this minimal wrapper.
    }
}
