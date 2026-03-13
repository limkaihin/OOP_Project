package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.app.engine.EngineContext;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.scene.AbstractBaseScene;

public final class MenuScene extends AbstractBaseScene {
    private final EngineContext ctx;
    private SpriteBatch batch;
    private BitmapFont font;

    public MenuScene(EngineContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onLoad() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        ctx.ioManager.log("MenuScene", "Loaded");
    }

    @Override
    public void update(float dt) {
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.CONFIRM)) {
            ctx.sceneManager.switchTo(new TransitionScene(ctx, new LevelSelectScene(ctx), 0.6f));
        }
    }

    @Override
    public void render() {
        ctx.renderer.end(); // Temporarily stop ShapeRenderer triggered by GameMaster

        batch.begin();
        font.getData().setScale(2.5f);
        font.draw(batch, "TRAIN RUSH!!!", 200, Gdx.graphics.getHeight() - 50);

        font.getData().setScale(1.5f);
        font.draw(batch,
                "How to Play:\n\n1. Wait for people to leave the train!\n2. Don't push them or you'll bounce away!\n3. Run into the train before time runs out!\n\nLevels get harder! Can you beat all 10?\n\nPress ENTER to Start!",
                50, Gdx.graphics.getHeight() - 150);
        batch.end();

        ctx.renderer.begin(); // Restart ShapeRenderer so GameMaster doesn't crash on end()
    }

    @Override
    public void onUnload() {
        if (batch != null)
            batch.dispose();
        if (font != null)
            font.dispose();
    }
}