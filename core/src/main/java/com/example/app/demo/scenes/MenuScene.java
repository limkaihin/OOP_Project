package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.example.app.demo.render.LibGdxFont;
import com.example.app.engine.EngineContext;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.render.EngineColor;
import com.example.app.engine.scene.AbstractBaseScene;

public final class MenuScene extends AbstractBaseScene {

    private final EngineContext ctx;
    private LibGdxFont font;
    private LibGdxFont bigFont;
    private final GlyphLayout layout = new GlyphLayout();

    private final float W, H;

    private static final EngineColor COL_BG = new EngineColor(0.06f, 0.06f, 0.10f, 1f);
    private static final EngineColor COL_PRESSTEXT = new EngineColor(0.96f, 0.77f, 0.09f, 1f);

    public MenuScene(EngineContext ctx) {
        this.ctx = ctx;
        this.W = ctx.getConfig().width;
        this.H = ctx.getConfig().height;
    }

    @Override
    public void onLoad() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Oswald-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter smallParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParams.size = 18;
        font = new LibGdxFont(generator.generateFont(smallParams));
        
        FreeTypeFontGenerator.FreeTypeFontParameter bigParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bigParams.size = 72;
        bigFont = new LibGdxFont(generator.generateFont(bigParams));

        generator.dispose();
        font.setColor(EngineColor.WHITE);

        ctx.getIoManager().log("MenuScene", "Loaded");
    }

    @Override
    public void update(float dt) {
        if (ctx.getIoManager().getInputHandler().getState().isJustPressed(InputAction.CONFIRM)) {
            ctx.getSceneManager().switchTo(new TransitionScene(ctx, new LevelSelectScene(ctx), 1.5f));
        }
    }

    @Override
    public void render() {
        // Dark background
        ctx.getRenderer().drawRect(0, 0, W, H, COL_BG);
    }

    @Override
    public void renderHud() {
        // Title
        bigFont.setColor(EngineColor.WHITE);
        layout.setText(bigFont.bitmapFont, "TRAIN RUSH!!!");
        ctx.getRenderer().drawText(bigFont, "TRAIN RUSH!!!", W / 2f - layout.width / 2f, H - 50f);

        // Instructions
        String[] lines = {
            "Avoid passengers and board the train before time runs out!",
            "Levels progressively get harder!",
            "",
            "How to Play:",
            "WASD - Move your character",
            "3 Hits - Game over",
            "E - Open music settings",
            "",
            "Can you beat all the levels?",
            "Press ENTER to Start!"
        };

        float lineY = H - 160f;
        for (String line : lines) {
            if (line.isEmpty()) {
                lineY -= 14f;
                continue;
            }
            layout.setText(font.bitmapFont, line);
            font.setColor(line.startsWith("Press") 
                ? COL_PRESSTEXT 
                : EngineColor.WHITE);
            ctx.getRenderer().drawText(font, line, W / 2f - layout.width / 2f, lineY);
            lineY -= layout.height + 10f;
        }

        font.setColor(EngineColor.WHITE);
    }

    @Override
    public void onUnload() {
        if (font != null) font.dispose();
        if (bigFont != null) bigFont.dispose();
    }
}