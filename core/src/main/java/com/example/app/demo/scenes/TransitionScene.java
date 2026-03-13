package com.example.app.demo.scenes;

import com.example.app.engine.EngineContext;
import com.example.app.engine.scene.AbstractBaseScene;
import com.example.app.engine.scene.Scene;

public final class TransitionScene extends AbstractBaseScene {
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
        ctx.ioManager.log("TransitionScene", "Transition...");
    }

    @Override
    public void update(float dt) {
        t += dt;
        if (t >= duration) {
            ctx.sceneManager.switchTo(next);
        }
    }

    @Override
    public void render() {
        float alpha = Math.min(1f, t / duration);
        ctx.renderer.draw("TRANSITION",0 , 0, alpha, 0, 0);
    }
}