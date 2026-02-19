package com.example.app.engine.io;

public class IOManager {

    private final InputHandler inputHandler;
    private final OutputHandler outputHandler;
    //to split up the IOManager into input and output handlers  
    public IOManager(InputHandler inputHandler, OutputHandler outputHandler) {
        this.inputHandler = (inputHandler == null) ? new InputHandler(new InputBinding()) : inputHandler;
        this.outputHandler = (outputHandler == null) ? new OutputHandler(null, null) : outputHandler;
    }
    //get the input handler
    public InputHandler getInputHandler() {
        return inputHandler;
    }
    //get the output handler
    public OutputHandler getOutputHandler() {
        return outputHandler;
    }
    //update the input handler
    public void update(float deltaTime) {
        inputHandler.update(deltaTime);
    }
    //log the message to the output handler
    public void log(String tag, String message) {
        outputHandler.log(tag, message);
    }
    //overiding used to pass the message to the output handler
    public void error(String tag, String message) {
        outputHandler.error(tag, message);
    }
    //overiding used to pass the throwable to the output handler
    public void error(String tag, String message, Throwable throwable) {
        outputHandler.error(tag, message, throwable);
    }
    //plays music
    public void playMusic(String filePath) {
        outputHandler.playMusic(filePath);
    }
    //plays sound
    public void playSound(String filePath) {
        outputHandler.playSound(filePath);
    }
    //stop music
    public void stopMusic() {
        outputHandler.stopMusic();
    }
    //dispose the output handler
    public void dispose() {
        outputHandler.dispose();
    }
}
