package com.example.app.engine.io;

// One logged error entry.
public class ErrorEntry {
    private final long timestamp;
    private final String tag;
    private final String message;

    public ErrorEntry(String tag, String message) {
        this.timestamp = System.currentTimeMillis();
        this.tag = tag;
        this.message = message;
    }

    public long getTimestamp() { return timestamp; }
    public String getTag() { return tag; }
    public String getMessage() { return message; }

    public String formattedTimeStamp() {
        return String.valueOf(timestamp);
    }
    //returns the error entry as a string
    @Override
    public String toString() {
        return "[" + formattedTimeStamp() + "][" + tag + "] " + message;
    }
}
