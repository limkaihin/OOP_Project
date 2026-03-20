package com.example.app.demo.progress;

import com.example.app.engine.progress.IProgress;

public class GameProgress implements IProgress {
    private int maxUnlockedLevel = 1;

    @Override
    public int getMaxUnlockedLevel() { 
        return maxUnlockedLevel;
    }

    @Override
    public void unlockNextLevel(int currentLevel) {
        maxUnlockedLevel = Math.max(maxUnlockedLevel, currentLevel + 1);
    }

    @Override
    public void reset() {
        maxUnlockedLevel = 1;
    }
}
