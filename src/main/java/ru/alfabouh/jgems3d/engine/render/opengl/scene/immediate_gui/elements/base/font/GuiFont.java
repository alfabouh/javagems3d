package ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.base.font;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.alfabouh.jgems3d.proxy.exception.JGemsException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GuiFont {
    public static Set<GuiFont> allCreatedFonts = new HashSet<>();
    private static int globalFonts = 0;
    private final FontCode fontCode;
    private final Map<Character, CharInfo> charMap = new HashMap<>();
    private TextureSample texture;
    private int height;
    private int width;

    public GuiFont(ResourceCache ResourceCache, Font font, FontCode fontCode) {
        this.fontCode = fontCode;
        this.initFontTexture(font);
        GuiFont.allCreatedFonts.add(this);
        if (ResourceCache != null) {
            ResourceCache.addObjectInBuffer("font" + GuiFont.globalFonts++, this.getTexture());
        }
    }

    public GuiFont(Font font, FontCode fontCode) {
        this(null, font, fontCode);
    }

    private void initFontTexture(Font font) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setFont(font);
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
        graphics2D.setFont(font);
        this.setFontParams(graphics2D);
        graphics2D.drawString(chars, 0, fontMetrics.getAscent());
        graphics2D.dispose();

        InputStream inputStream;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new JGemsException(e.getMessage());
        }
        this.texture = TextureSample.createTextureIS("font", inputStream, false, GL30.GL_CLAMP_TO_EDGE);
    }

    protected void setFontParams(Graphics2D graphics2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.WHITE);
    }

    public void cleanUp() {
        this.getTexture().clear();
    }

    public CharInfo getCharInfo(char c) {
        return this.charMap.get(c);
    }

    public TextureSample getTexture() {
        return this.texture;
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
