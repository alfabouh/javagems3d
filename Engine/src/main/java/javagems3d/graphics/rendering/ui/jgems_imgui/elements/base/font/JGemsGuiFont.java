package javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.loading.samples.TexturesLoader;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsIOException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class JGemsGuiFont {
    private static int globalFonts = 0;
    private final FontCode fontCode;
    private final Map<Character, CharInfo> charMap = new HashMap<>();
    private ITexture2DProgram texture;
    private int height;
    private int width;

    public JGemsGuiFont(SystemResources systemResources, Font font, FontCode fontCode) {
        this.fontCode = fontCode;
        try {
            this.initFontTexture(systemResources, font);
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public JGemsGuiFont(Font font, FontCode fontCode) {
        this(null, font, fontCode);
    }

    private void initFontTexture(SystemResources systemResources, Font font) throws IOException {
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
            this.width += charInfo.width();
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
            throw new JGemsIOException(e);
        }
        if (systemResources != null) {
            this.texture = systemResources.createTexture(null, "font" + JGemsGuiFont.globalFonts++, inputStream, new ImageTexture.Properties(false, false, false, false, false));
        } else {
            this.texture = new TexturesLoader(null, "font" + JGemsGuiFont.globalFonts++).createImageTexture(new ImageTexture.Properties(false, false, false, false, false), inputStream);
        }
        inputStream.close();
    }

    protected void setFontParams(Graphics2D graphics2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.WHITE);
    }

    public CharInfo getCharInfo(char c) {
        return this.charMap.get(c);
    }

    public ITexture2DProgram getTexture() {
        return this.texture;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void clear() {
        this.getTexture().clear();
    }

    public record CharInfo(int startX, int width) {
    }
}
