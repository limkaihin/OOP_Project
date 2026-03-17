package com.example.app.engine.io;

public class OutputHandler {

    private final AudioPlayer audioPlayer;
    private final ErrorLogger errorLogger;

    public OutputHandler(AudioPlayer audioPlayer, ErrorLogger errorLogger) {
        this.audioPlayer = (audioPlayer == null) ? new AudioPlayer() : audioPlayer;
        this.errorLogger = (errorLogger == null) ? new ErrorLogger() : errorLogger;
    }

    // ---- logging ----
    public void log(String tag, String message)                        { errorLogger.log(tag, message); }
    public void error(String tag, String message)                      { errorLogger.error(tag, message); }
    public void error(String tag, String message, Throwable throwable) { errorLogger.error(tag, message, throwable); }

    // ---- music ----
    public void playMusic(String filePath)          { audioPlayer.playMusic(filePath); }
    public void stopMusic()                         { audioPlayer.stopMusic(); }
    public boolean isMusicEnabled()                 { return audioPlayer.isMusicEnabled(); }
    public void setMusicEnabled(boolean enabled)    { audioPlayer.setMusicEnabled(enabled); }
    public float getMusicVolume()                   { return audioPlayer.getMusicVolume(); }
    public void setMusicVolume(float v)             { audioPlayer.setMusicVolume(v); }

    // ---- sfx ----
    public void playSound(String filePath)          { audioPlayer.playSound(filePath); }
    public void stopSound()                         { audioPlayer.stopSound(); }
    public boolean isSfxEnabled()                   { return audioPlayer.isSfxEnabled(); }
    public void setSfxEnabled(boolean enabled)      { audioPlayer.setSfxEnabled(enabled); }
    public float getSfxVolume()                     { return audioPlayer.getSfxVolume(); }
    public void setSfxVolume(float v)               { audioPlayer.setSfxVolume(v); }

    // ---- legacy volume ----
    public void setVolume(float volume)             { audioPlayer.setMasterVolume(volume); }

    // ---- accessors ----
    public AudioPlayer getAudioPlayer()             { return audioPlayer; }
    public ErrorLogger getErrorLogger()             { return errorLogger; }

    public void dispose()                           { stopMusic(); }
}
