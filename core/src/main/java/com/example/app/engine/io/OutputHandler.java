package com.example.app.engine.io;

public class OutputHandler {

    private final AudioPlayer audioPlayer;
    private final ErrorLogger errorLogger;

    // initializes the output handler
    public OutputHandler(AudioPlayer audioPlayer, ErrorLogger errorLogger) {
        this.audioPlayer = (audioPlayer == null) ? new AudioPlayer() : audioPlayer;
        this.errorLogger = (errorLogger == null) ? new ErrorLogger() : errorLogger;
    }

    // log the message to the error logger
    public void log(String tag, String message) {
        errorLogger.log(tag, message);
    }

    // overiding used to pass the message to the error logger
    public void error(String tag, String message) {
        errorLogger.error(tag, message);
    }

    // overiding used to pass the message to the error logger
    public void error(String tag, String message, Throwable throwable) {
        errorLogger.error(tag, message, throwable);
    }

    // plays music
    public void playMusic(String filePath) {
        audioPlayer.playMusic(filePath);
    }

    // plays sound
    public void playSound(String filePath) {
        audioPlayer.playSound(filePath);
    }

    // stop music
    public void stopMusic() {
        audioPlayer.stopMusic();
    }

    // stop sound
    public void stopSound() {
        audioPlayer.stopSound();
    }

    // set the volume
    public void setVolume(float volume) {
        audioPlayer.setMasterVolume(volume);
    }

    // get the audio player
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    // get the error logger
    public ErrorLogger getErrorLogger() {
        return errorLogger;
    }

    // dispose the audio player
    public void dispose() {
        stopMusic();
    }
}
