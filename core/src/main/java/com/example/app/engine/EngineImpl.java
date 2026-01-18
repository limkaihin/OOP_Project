package com.example.app.engine;

import com.example.app.engine.io.InputAction;
import com.example.app.engine.scene.Scene;

public final class EngineImpl implements Engine {

    private final EngineContext ctx;

    public EngineImpl(EngineContext ctx) {
        this.ctx = ctx;
    }

    public EngineContext context() { return ctx; }

    @Override
    public void init() {
        Scene current = ctx.sceneManager.current();
        if (current != null) current.onEnter();
    }

    @Override
    public void update(float realDt) {
        // Clear render queue each frame; scenes submit draw commands.
        ctx.renderQueue.clear();

        // Input always updates on real time
        ctx.inputManager.update(realDt);

        // Engine-level pause/step controls (generic)
        if (ctx.inputManager.getState().isJustPressed(InputAction.PAUSE)) {
            ctx.clock.togglePause();
            ctx.outputManager.log("Engine", ctx.clock.isPaused() ? "Paused" : "Resumed");
        }
        if (ctx.clock.isPaused() && ctx.inputManager.getState().isJustPressed(InputAction.CONFIRM)) {
            ctx.clock.requestStep();
        }

        int steps = ctx.clock.consumeSteps(realDt, ctx.config.maxSubSteps);
        float dt = ctx.clock.fixedDt();

        for (int i = 0; i < steps; i++) {
            Scene current = ctx.sceneManager.current();
            if (current != null) current.update(dt);

            ctx.movementManager.update(dt, ctx.entityManager.getAll());
            ctx.collisionManager.update(dt, ctx.entityManager.getAll());
            ctx.entityManager.update(dt);
        }
    }

    @Override
    public void dispose() {
        while (ctx.sceneManager.size() > 0) ctx.sceneManager.pop();
        ctx.outputManager.log("Engine", "Disposed");
    }
}