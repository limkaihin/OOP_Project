package com.example.app.engine.io;

/**
 * IOManager (UML-aligned): owns input + output subsystems.
 */
public class IOManager {

    private final InputHandler inputHandler;
    private final OutputHandler outputHandler;

    public IOManager(InputHandler inputHandler, OutputHandler outputHandler) {
        this.inputHandler = (inputHandler == null) ? new InputHandler(new InputBinding()) : inputHandler;
        this.outputHandler = (outputHandler == null) ? new OutputHandler(null, null) : outputHandler;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public OutputHandler getOutputHandler() {
        return outputHandler;
    }

    public void update(float deltaTime) {
        inputHandler.update(deltaTime);
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

    public void playMusic(String filePath) {
        outputHandler.playMusic(filePath);
    }

    public void playSound(String filePath) {
        outputHandler.playSound(filePath);
    }

    public void stopMusic() {
        outputHandler.stopMusic();
    }

    public void dispose() {
        outputHandler.dispose();
    }
}
