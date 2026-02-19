package com.example.app.engine.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorLogger {

    private final List<ErrorEntry> errorLogs = new ArrayList<>();
    private int maxErrors = 1000;

    public ErrorLogger() {}

    public ErrorLogger(int maxErrors) {
        this.maxErrors = Math.max(1, maxErrors);
    }
    //logs a message
    public void log(String tag, String message) {
        System.out.println("[LOG][" + tag + "] " + message);
    }
    //logs an error
    public void error(String tag, String message) {
        add(new ErrorEntry(tag, message));
        System.err.println("[ERR][" + tag + "] " + message);
    }
    //logs an error with a throwable
    public void error(String tag, String message, Throwable throwable) {
        error(tag, message + (throwable == null ? "" : (" :: " + throwable)));
    }
    //adds an error to the error log
    private void add(ErrorEntry entry) {
        if (errorLogs.size() >= maxErrors) {
            errorLogs.remove(0);
        }
        errorLogs.add(entry);
    }
    //returns the error log
    public List<ErrorEntry> getErrors() {
        return Collections.unmodifiableList(errorLogs);
    }
    //sets the max errors
    public void setMaxErrors(int maxErrors) {
        this.maxErrors = Math.max(1, maxErrors);
    }
    //exports the error log to a file
    //if the file path is null, do nothing
    public void exportToFile(String filePath) {
        if (filePath == null) return;
        try (FileWriter w = new FileWriter(filePath)) {
            for (ErrorEntry e : errorLogs) {
                w.write(e.toString());
                w.write("\n");
            }
        } catch (IOException ex) {
            System.err.println("[ERR][ErrorLogger] Failed to export: " + ex);
        }
    }
}
