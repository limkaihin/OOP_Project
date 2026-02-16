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
    private String lastMusicFilePath;
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
        this.lastMusicFilePath = filePath;

        // Create and play the sound
        currentMusic = new Sound(filePath);
        if (currentMusic != null) {
            currentMusic.setVolume(masterVolume);
            currentMusic.play();
        }
    }

    public String getLastMusicFilePath() {
        return lastMusicFilePath;
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

    public Sound getCurrentMusic() {
        if (currentMusic == null) {
            return null;
        }
        return currentMusic;
    }

    public void dispose() {
        stopMusic();
        for (Sound sound : activeSounds) {
            sound.dispose();
        }
        activeSounds.clear();
    }
}