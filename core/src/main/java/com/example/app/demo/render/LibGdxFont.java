package com.example.app.demo.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.example.app.engine.render.InterfaceFont;

public final class LibGdxFont implements InterfaceFont {
    public final BitmapFont bitmapFont;

    public LibGdxFont(BitmapFont bitmapFont) {
        this.bitmapFont = bitmapFont;
    }

    public void setColor(Color color) {
        bitmapFont.setColor(color);
    }

    public void dispose() {
        bitmapFont.dispose();
    }
}