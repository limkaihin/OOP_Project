package com.example.app.demo.scenes;

import com.example.app.engine.EngineContext;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.render.RenderCommand;
import com.example.app.engine.scene.BaseScene;

public final class MenuScene extends BaseScene {

    private final EngineContext ctx;
    private int spawnCount = 50;

    public MenuScene(EngineContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onLoad() {
        ctx.outputManager.log("MenuScene", "Loaded");
    }

    @Override
    public void update(float dt) {
        // UI overlay (text is drawn in Main; here we only draw a subtle background)
        ctx.renderQueue.submit(RenderCommand.fullscreenFade(0f, 0f, 0f, 0.25f));

        if (ctx.inputManager.getState().isJustPressed(InputAction.MOVE_LEFT))  spawnCount = 50;
        if (ctx.inputManager.getState().isJustPressed(InputAction.MOVE_UP))    spawnCount = 200;
        if (ctx.inputManager.getState().isJustPressed(InputAction.MOVE_RIGHT)) spawnCount = 400;

        if (ctx.inputManager.getState().isJustPressed(InputAction.CONFIRM)) {
            ctx.sceneManager.switchTo(new TransitionScene(ctx, new SandboxScene(ctx, spawnCount), 0.6f));
        }
    }

    public int getSpawnCount() { return spawnCount; }
}