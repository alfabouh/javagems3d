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

package javagems3d.graphics.rendering.programs.textures;

import javagems3d.system.resources.assets.texturing.ext.IBindlessTexture;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

public class TextureProgram implements ITextureProgram, IBindlessTexture {
    private int textureId;
    private long bindlessHandler;

    public TextureProgram() {
        this.bindlessHandler = 0;
        this.textureId = 0;
    }

    public void createTexture(Vector2i size, int textureFormat, int internalFormat, int filtering_mag, int filtering_min, int compareMode, int compareFunc, int clamp_s, int clamp_t, float[] borderColor) {
        this.createTexture(size, textureFormat, internalFormat, filtering_mag, filtering_min, compareMode, compareFunc, clamp_s, clamp_t, borderColor, null);
    }

    public void createTexture(Vector2i size, int textureFormat, int internalFormat, int filtering_mag, int filtering_min, int compareMode, int compareFunc, int clamp_s, int clamp_t, float[] borderColor, FloatBuffer pixels) {
        this.textureId = GL46.glGenTextures();
        this.bindTexture(GL46.GL_TEXTURE_2D);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, textureFormat, size.x, size.y, 0, internalFormat, GL46.GL_FLOAT, pixels);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, filtering_mag);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, filtering_min);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, clamp_s);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, clamp_t);
        if (borderColor != null) {
            GL46.glTexParameterfv(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_BORDER_COLOR, borderColor);
        }
        this.unBindTexture();

        this.bindlessHandler = this.createBindingHandler(this.getTextureId());
        this.createARB64Handling();
    }

    @Override
    public void clear() {
        this.removeARB64Handling();
        GL46.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public long getBindingHandler() {
        return this.bindlessHandler;
    }
}
