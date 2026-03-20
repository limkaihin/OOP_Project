package com.example.app.engine.progress;

public interface IProgress {
    int getMaxUnlockedLevel();
    void unlockNextLevel(int currentLevel);
    void reset();
}
