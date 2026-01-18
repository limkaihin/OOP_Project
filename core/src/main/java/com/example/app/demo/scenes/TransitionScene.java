package com.example.app.demo.scenes;

import com.example.app.engine.EngineContext;
import com.example.app.engine.render.RenderCommand;
import com.example.app.engine.scene.BaseScene;
import com.example.app.engine.scene.Scene;

public final class TransitionScene extends BaseScene {

    private final EngineContext ctx;
    private final Scene next;
    private final float duration;
    private float t = 0f;

    public TransitionScene(EngineContext ctx, Scene next, float durationSeconds) {
        this.ctx = ctx;
        this.next = next;
        this.duration = Math.max(0.1f, durationSeconds);
    }

    @Override
    public void onLoad() {
        ctx.outputManager.log("TransitionScene", "Transition...");
    }

    @Override
    public void update(float dt) {
        t += dt;
        float alpha = Math.min(1f, t / duration);
        ctx.renderQueue.submit(RenderCommand.fullscreenFade(0f, 0f, 0f, alpha));

        if (t >= duration) {
            ctx.sceneManager.switchTo(next);
        }
    }
}