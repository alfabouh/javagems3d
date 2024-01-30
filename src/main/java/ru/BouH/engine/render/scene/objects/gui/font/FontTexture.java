package ru.BouH.engine.render.scene.objects.gui.font;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontTexture {
    private final Font font;
    private final FontCode fontCode;
    private final Map<Character, CharInfo> charMap = new HashMap<>();
    private TextureSample texture;
    private int height;
    private int width;

    public FontTexture(Font font, FontCode fontCode) {
        this.font = font;
        this.fontCode = fontCode;
        this.initFontTexture();
    }

    private void initFontTexture() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setFont(this.getFont());
        FontMetrics fontMetrics = graphics2D.getFontMetrics();

        String chars = this.fontCode.getChars();
        this.width = 0;
        this.height = 0;
        for (char c : chars.toCharArray()) {
            CharInfo charInfo = new CharInfo(this.getWidth(), fontMetrics.charWidth(c));
            this.charMap.put(c, charInfo);
            this.width += charInfo.getWidth();
            this.height = Math.max(this.getHeight(), fontMetrics.getHeight());
        }
        graphics2D.dispose();

        image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setFont(this.getFont());
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(chars, 0, fontMetrics.getAscent());
        graphics2D.dispose();

        InputStream inputStream = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            Game.getGame().getLogManager().error(e.getMessage());
        }
        this.texture = TextureSample.createTextureIS("font", inputStream);
    }

    public CharInfo getCharInfo(char c) {
        return this.charMap.get(c);
    }

    public TextureSample getTexture() {
        return this.texture;
    }

    public Font getFont() {
        return this.font;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public static class CharInfo {
        private final int startX;
        private final int width;

        public CharInfo(int startX, int width) {
            this.startX = startX;
            this.width = width;
        }

        public int getStartX() {
            return this.startX;
        }

        public int getWidth() {
            return this.width;
        }
    }
}
