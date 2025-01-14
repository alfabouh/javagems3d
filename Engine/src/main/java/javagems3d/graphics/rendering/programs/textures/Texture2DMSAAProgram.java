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

import javagems3d.graphics.rendering.programs.textures.cache.TexturesSamplersCachingProgram;
import javagems3d.graphics.rendering.programs.textures.ext.ITextureBindless;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

public class Texture2DMSAAProgram implements ITextureProgram, ITextureBindless {
    private final int msaa;
    private int textureId;
    private int samplerId;
    private long bindlessHandler;

    public Texture2DMSAAProgram(int msaa) {
        this.msaa = msaa;
        this.bindlessHandler = 0;
        this.textureId = 0;
        this.samplerId = 0;
    }

    public void createTexture(Vector2i size, @NotNull Texture2DProgram.Properties properties, FloatBuffer pixels) {
        this.textureId = GL46.glGenTextures();
        this.bindTexture();
        GL46.glTexImage2DMultisample(this.getTextureAttachment(), this.msaa, properties.getInternalFormat(), size.x, size.y, true);
        this.samplerId = this.createSampler(properties);
        this.unBindTexture();

        this.bindlessHandler = this.createBindingHandler(this.getTextureId(), this.getSamplerId());
        this.createARB64Handling();
    }

    protected int createSampler(@NotNull Texture2DProgram.Properties properties) {
        int sampler = TexturesSamplersCachingProgram.createSamplerId(Texture2DProgram.class, properties.getHash());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MAG_FILTER, properties.getFilteringMag());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_MIN_FILTER, properties.getFilteringMin());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_COMPARE_MODE, properties.getCompareMode());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_COMPARE_FUNC, properties.getCompareFunc());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_S, properties.getClampS());
        GL46.glSamplerParameteri(this.getSamplerId(), GL46.GL_TEXTURE_WRAP_T, properties.getClampT());
        if (properties.getBorderColor() != null) {
            GL46.glSamplerParameterfv(this.getSamplerId(), GL46.GL_TEXTURE_BORDER_COLOR, properties.getBorderColor());
        }
        return sampler;
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
    public int getTextureAttachment() {
        return GL46.GL_TEXTURE_2D_MULTISAMPLE;
    }

    @Override
    public int getSamplerId() {
        return this.samplerId;
    }

    @Override
    public long getBindingHandler() {
        return this.bindlessHandler;
    }
}