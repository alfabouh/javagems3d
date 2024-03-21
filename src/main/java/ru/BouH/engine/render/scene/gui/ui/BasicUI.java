package ru.BouH.engine.render.scene.gui.ui;

import org.joml.Vector2d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.gui.font.GuiFont;
import ru.BouH.engine.render.screen.Screen;

public interface BasicUI {
    void render(double partialTicks);
    void clear();
    boolean isVisible();
    ShaderManager getCurrentShader();

    static float[] HEX2RGB(int hex) {
        int r = (hex & 0xFFFFFF) >> 16;
        int g = (hex & 0xFFFF) >> 8;
        int b = hex & 0xFF;
        return new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
    }

    static Vector2d getScaledPictureDimensions(TextureSample textureSample, float scale) {
        if (textureSample == null || !textureSample.isValid()) {
            return new Vector2d(0.0f);
        }
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        Vector2d WH = new Vector2d(width / Screen.defaultW, height / Screen.defaultH).mul(scale);
        double picScale = Math.min(WH.x, WH.y);
        return new Vector2d(textureSample.getWidth() * picScale, textureSample.getHeight() * picScale);
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
}
