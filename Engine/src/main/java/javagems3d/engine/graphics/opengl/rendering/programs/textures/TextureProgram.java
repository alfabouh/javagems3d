/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.graphics.opengl.rendering.programs.textures;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import java.nio.FloatBuffer;

public class TextureProgram implements ITextureProgram {
    private int textureId;

    public TextureProgram() {
    }

    public void createTexture(Vector2i size, int textureFormat, int internalFormat, int filtering_mag, int filtering_min, int compareMode, int compareFunc, int clamp_s, int clamp_t, float[] borderColor) {
        this.createTexture(size, textureFormat, internalFormat, filtering_mag, filtering_min, compareMode, compareFunc, clamp_s, clamp_t, borderColor, null);
    }

    public void createTexture(Vector2i size, int textureFormat, int internalFormat, int filtering_mag, int filtering_min, int compareMode, int compareFunc, int clamp_s, int clamp_t, float[] borderColor, FloatBuffer pixels) {
        this.textureId = GL30.glGenTextures();
        this.bindTexture(GL43.GL_TEXTURE_2D);
        GL30.glTexImage2D(GL43.GL_TEXTURE_2D, 0, textureFormat, size.x, size.y, 0, internalFormat, GL30.GL_FLOAT, pixels);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, filtering_mag);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, filtering_min);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D, GL30.GL_TEXTURE_COMPARE_MODE, compareMode);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D, GL30.GL_TEXTURE_COMPARE_FUNC, compareFunc);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, clamp_s);
        GL30.glTexParameteri(GL43.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, clamp_t);
        if (borderColor != null) {
            GL30.glTexParameterfv(GL43.GL_TEXTURE_2D, GL30.GL_TEXTURE_BORDER_COLOR, borderColor);
        }
        this.unBindTexture();
    }

    @Override
    public void cleanUp() {
        this.unBindTexture();
        GL30.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    public int getTextureId() {
        return this.textureId;
    }
}
