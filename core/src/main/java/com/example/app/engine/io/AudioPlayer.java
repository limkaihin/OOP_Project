package com.example.app.engine.io;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayer {

    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;

    private float musicVolume = 0.5f;
    private float sfxVolume = 0.5f;

    private Sound currentMusic;
    private String lastMusicFilePath;
    private final List<Sound> activeSounds = new ArrayList<>();

    public AudioPlayer() {
    }

    // ---------------------------------------------------------------- music

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopMusic();
        } else if (lastMusicFilePath != null) {
            // Restart the last track immediately
            playMusicInternal(lastMusicFilePath);
        }
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        if (currentMusic != null)
            currentMusic.setVolume(this.musicVolume);
    }

    public void playMusic(String filePath) {
        lastMusicFilePath = filePath;
        if (!musicEnabled)
            return;
        playMusicInternal(filePath);
    }

    private void playMusicInternal(String filePath) {
        stopMusic();
        currentMusic = new Sound(filePath);
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
            currentMusic.play();
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    public String getLastMusicFilePath() {
        return lastMusicFilePath;
    }

    public Sound getCurrentMusic() {
        return currentMusic;
    }

    // ---------------------------------------------------------------- sfx

    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    public void setSfxEnabled(boolean enabled) {
        this.sfxEnabled = enabled;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    public void playSound(String filePath) {
        if (!sfxEnabled)
            return;
        Sound sound = new Sound(filePath);
        if (sound != null) {
            sound.setVolume(sfxVolume);
            sound.play();
            activeSounds.add(sound);
        }
    }

    public void stopSound() {
        // Legacy helper — stops all active oneshot sounds
        for (Sound s : activeSounds)
            s.stop();
        activeSounds.clear();
    }

    // ---------------------------------------------------------------- legacy
    // master volume helpers (kept for backward compatibility)

    /** Returns music volume (was "masterVolume"). */
    public float getMasterVolume() {
        return musicVolume;
    }

    public void setMasterVolume(float volume) {
        setMusicVolume(volume);
        setSfxVolume(volume);
    }

    public void increaseVolume(float amount) {
        setMusicVolume(musicVolume + amount);
    }

    public void decreaseVolume(float amount) {
        setMusicVolume(musicVolume - amount);
    }

    public int getVolumePercentage() {
        return (int) (musicVolume * 100);
    }

    // ----------------------------------------------------------------

    public void dispose() {
        stopMusic();
        for (Sound s : activeSounds)
            s.dispose();
        activeSounds.clear();
    }
}