package com.example.app.engine.io;

/**
 * OutputHandler (UML-aligned): logging + audio + renderer lifecycle.
 */
public class OutputHandler {

    private final AudioPlayer audioPlayer;
    private final ErrorLogger errorLogger;

    public OutputHandler(AudioPlayer audioPlayer, ErrorLogger errorLogger) {
        this.audioPlayer = (audioPlayer == null) ? new AudioPlayer() : audioPlayer;
        this.errorLogger = (errorLogger == null) ? new ErrorLogger() : errorLogger;
    }

    public void log(String tag, String message) {
        errorLogger.log(tag, message);
    }

    public void error(String tag, String message) {
        errorLogger.error(tag, message);
    }

    public void error(String tag, String message, Throwable throwable) {
        errorLogger.error(tag, message, throwable);
    }

    public void playMusic(String filePath) {
        audioPlayer.playMusic(filePath);
    }

    public void playSound(String filePath) {
        audioPlayer.playSound(filePath);
    }

    public void stopMusic() {
        audioPlayer.stopMusic();
    }

    public void setVolume(float volume) {
        audioPlayer.setMasterVolume(volume);
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public ErrorLogger getErrorLogger() {
        return errorLogger;
    }

    public void dispose() {
        stopMusic();
    }
}
