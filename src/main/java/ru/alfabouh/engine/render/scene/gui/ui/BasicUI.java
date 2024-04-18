package ru.alfabouh.engine.render.scene.gui.ui;

import org.joml.Vector2f;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.controller.ControllerDispatcher;
import ru.alfabouh.engine.game.controller.input.IController;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.render.scene.gui.font.GuiFont;
import ru.alfabouh.engine.render.screen.Screen;

public interface BasicUI {
    static float[] HEX2RGB(int hex) {
        int r = (hex & 0xFFFFFF) >> 16;
        int g = (hex & 0xFFFF) >> 8;
        int b = hex & 0xFF;
        return new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
    }

    static Vector2f getScreenNormalizedScaling() {
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        float f1 = (float) (width / Screen.defaultW);
        float f2 = (float) (height / Screen.defaultH);
        float f1_r = (float) Math.max(Math.ceil(f1 * 2.0f) / 2.0f, 1.0f);
        float f2_r = (float) Math.max(Math.ceil(f2 * 2.0f) / 2.0f, 1.0f);
        float f3 = Math.min(f1_r, f2_r);
        return new Vector2f(f3);
    }

    static float getFontHeight(GuiFont fontTexture) {
        return fontTexture.getHeight();
    }

    static float getTextWidth(GuiFont fontTexture, String text) {
        char[] chars = text.toCharArray();
        float startX = 0.0f;
        for (final char aChar : chars) {
            GuiFont.CharInfo charInfo = fontTexture.getCharInfo(aChar);
            startX += charInfo.getWidth();
        }
        return startX;
    }

    void render(double partialTicks);

    void clear();

    boolean isVisible();

    ShaderManager getCurrentShader();

    default IController getController() {
        return Game.getGame().getScreen().getControllerDispatcher().getCurrentController();
    }
}
