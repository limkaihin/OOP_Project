package com.example.app.engine.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects logs/errors for debugging (UML-aligned).
 */
public class ErrorLogger {

    private final List<ErrorEntry> errorLogs = new ArrayList<>();
    private int maxErrors = 1000;

    public ErrorLogger() {}

    public ErrorLogger(int maxErrors) {
        this.maxErrors = Math.max(1, maxErrors);
    }

    public void log(String tag, String message) {
        // For now, treat log entries as non-fatal; keep a smaller in-memory record.
        // (You can extend this to store info logs too if desired.)
        System.out.println("[LOG][" + tag + "] " + message);
    }

    public void error(String tag, String message) {
        add(new ErrorEntry(tag, message));
        System.err.println("[ERR][" + tag + "] " + message);
    }

    public void error(String tag, String message, Throwable throwable) {
        error(tag, message + (throwable == null ? "" : (" :: " + throwable)));
    }

    private void add(ErrorEntry entry) {
        if (errorLogs.size() >= maxErrors) {
            errorLogs.remove(0);
        }
        errorLogs.add(entry);
    }

    public List<ErrorEntry> getErrors() {
        return Collections.unmodifiableList(errorLogs);
    }

    public void setMaxErrors(int maxErrors) {
        this.maxErrors = Math.max(1, maxErrors);
    }

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
