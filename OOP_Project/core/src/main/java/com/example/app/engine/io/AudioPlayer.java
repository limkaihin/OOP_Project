package com.example.app.engine.io;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple audio manager (UML-aligned).
 * Uses {@link Sound} as a minimal wrapper.
 */
public class AudioPlayer {

    private float masterVolume = 0.5f;
    private Sound currentMusic;
    private final List<Sound> activeSounds = new ArrayList<>();

    public AudioPlayer() {
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float masterVolume) {
        this.masterVolume = Math.max(0f, Math.min(1f, masterVolume));

        // Update current music
        if (currentMusic != null)
            currentMusic.setVolume(this.masterVolume);

        // Update ALL active sounds
        for (Sound sound : activeSounds) {
            sound.setVolume(this.masterVolume);
        }
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
            activeSounds.add(sound);

            // Remove sound when done (simplified - in real app you'd need to check if
            // playing)
            // For now, we'll clean up old sounds periodically
            cleanupFinishedSounds();
        }
    }

    private void cleanupFinishedSounds() {
        // Remove sounds that are no longer playing
        activeSounds.removeIf(sound -> !sound.isPlaying());
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    public void increaseVolume(float amount) {
        setMasterVolume(masterVolume + amount);
    }

    public void decreaseVolume(float amount) {
        setMasterVolume(masterVolume - amount);
    }

    public int getVolumePercentage() {
        return (int) (masterVolume * 100);
    }

    public void dispose() {
        stopMusic();
        for (Sound sound : activeSounds) {
            sound.dispose();
        }
        activeSounds.clear();
    }
}