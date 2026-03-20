package com.example.app.engine.io;

public class IOManager {

    private final InputHandler inputHandler;
    private final OutputHandler outputHandler;

    public IOManager(InputHandler inputHandler, OutputHandler outputHandler) {
        this.inputHandler = (inputHandler == null) ? new InputHandler(new InputBinding()) : inputHandler;
        this.outputHandler = (outputHandler == null) ? new OutputHandler(null, null) : outputHandler;
    }

    // ---- input ----
    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public void update(float deltaTime) {
        inputHandler.update(deltaTime);
    }

    // ---- output ----
    public OutputHandler getOutputHandler() {
        return outputHandler;
    }

    public void log(String tag, String message) {
        outputHandler.log(tag, message);
    }

    public void error(String tag, String message) {
        outputHandler.error(tag, message);
    }

    public void error(String tag, String message, Throwable throwable) {
        outputHandler.error(tag, message, throwable);
    }

    // ---- music ----
    public void playMusic(String filePath) {
        outputHandler.playMusic(filePath);
    }

    public void stopMusic() {
        outputHandler.stopMusic();
    }

    public boolean isMusicEnabled() {
        return outputHandler.isMusicEnabled();
    }

    public void setMusicEnabled(boolean enabled) {
        outputHandler.setMusicEnabled(enabled);
    }

    public float getMusicVolume() {
        return outputHandler.getMusicVolume();
    }

    public void setMusicVolume(float v) {
        outputHandler.setMusicVolume(v);
    }

    // ---- sfx ----
    public void playSound(String filePath) {
        outputHandler.playSound(filePath);
    }

    public boolean isSfxEnabled() {
        return outputHandler.isSfxEnabled();
    }

    public void setSfxEnabled(boolean enabled) {
        outputHandler.setSfxEnabled(enabled);
    }

    public float getSfxVolume() {
        return outputHandler.getSfxVolume();
    }

    public void setSfxVolume(float v) {
        outputHandler.setSfxVolume(v);
    }

    // ---- dispose ----
    public void dispose() {
        outputHandler.dispose();
    }
}