package com.example.app.engine.util;

/**
 * Fixed-timestep + pause/step controller (engine-level, non-contextual).
 */
public final class EngineClock {
    private final float fixedDt;
    private float accumulator = 0f;

    private boolean paused = false;
    private boolean stepRequested = false;

    public EngineClock(float fixedDt) {
        this.fixedDt = fixedDt;
    }

    public void togglePause() { paused = !paused; }
    public boolean isPaused() { return paused; }

    public void requestStep() { stepRequested = true; }

    /**
     * Returns how many fixed updates to run this frame.
     * - If not paused: accumulates real dt and returns floor(accumulator/fixedDt).
     * - If paused: returns 1 only when stepRequested is true.
     */
    public int consumeSteps(float realDt, int maxSteps) {
        if (paused) {
            if (stepRequested) {
                stepRequested = false;
                return 1;
            }
            return 0;
        }

        accumulator += Math.max(0f, realDt);
        int steps = (int)(accumulator / fixedDt);
        if (steps > maxSteps) steps = maxSteps;

        accumulator -= steps * fixedDt;
        return steps;
    }

    public float fixedDt() { return fixedDt; }
}