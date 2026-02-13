package com.example.app.demo.scenes;

import com.example.app.engine.EngineContext;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.render.RenderCommand;
import com.example.app.engine.scene.AbstractBaseScene;

public final class MenuScene extends AbstractBaseScene {

    private final EngineContext ctx;
    private int spawnCount = 1;

    public MenuScene(EngineContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onLoad() {
        ctx.ioManager.log("MenuScene", "Loaded");
    }

    @Override
    public void update(float dt) {
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.CONFIRM)) {
            ctx.sceneManager.switchTo(new TransitionScene(ctx, new SandboxScene(ctx, spawnCount), 0.6f));
        }
    }

    @Override
    public void render() {
        ctx.renderQueue.clear();
        ctx.renderQueue.submit(RenderCommand.fullscreenFade(0.4f, 0.6f, 0.9f, 0.25f));
    }

    public int getSpawnCount() {
        return spawnCount;
    }
}