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

package javagems3d.system.resources.assets.material.samples;

import com.google.common.io.ByteStreams;
import org.joml.Vector2i;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.material.samples.base.ITextureSample;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureSample implements ITextureSample {
    private final String name;
    private final Params params;
    private Vector2i size;
    private int textureId;

    private TextureSample(String name, Vector2i size, ByteBuffer buffer, Params params) {
        this.name = name;
        this.size = size;
        this.params = params;
        this.registerTexture(buffer, params);
    }

    private TextureSample(JGemsPath fullPath, Params params) {
        this.name = fullPath.getFullPath();
        this.params = params;
        JGemsHelper.getLogger().log("Loading " + this.getName());
        try (InputStream inputStream = JGems3D.loadFileFromJar(fullPath)) {
            this.registerTexture(this.readTextureFromMemory(this.getName(), inputStream), params);
        } catch (JGemsIOException | IOException e) {
            throw new JGemsIOException(e);
        }
    }

    private TextureSample(String name, InputStream inputStream, Params params) {
        this.name = name;
        this.params = params;
        try {
            this.registerTexture(this.readTextureFromMemory(name, inputStream), params);
        } catch (JGemsIOException e) {
            throw new JGemsIOException(e);
        }
    }

    public static TextureSample registerTexture(ResourceCache resourceCache, JGemsPath fullPath, Params params) {
        TextureSample textureSample = new TextureSample(fullPath, params);
        if (resourceCache != null) {
            if (resourceCache.checkObjectInCache(fullPath)) {
                return (TextureSample) resourceCache.getCachedObject(fullPath);
            }
            if (textureSample.isValid()) {
                resourceCache.addObjectInBuffer(fullPath, textureSample);
            } else {
                throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
            }
        }
        return textureSample;
    }

    public static TextureSample registerTexture(ResourceCache resourceCache, String name, Vector2i size, ByteBuffer buffer, Params params) {
        TextureSample textureSample = new TextureSample(name, size, buffer, params);
        if (resourceCache != null) {
            if (resourceCache.checkObjectInCache(buffer.toString())) {
                return (TextureSample) resourceCache.getCachedObject(buffer.toString());
            }
            if (textureSample.isValid()) {
                resourceCache.addObjectInBuffer(buffer.toString(), textureSample);
            } else {
                throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
            }
        }
        return textureSample;
    }

    public static TextureSample registerTexture(ResourceCache resourceCache, String name, InputStream inputStream, Params params) {
        TextureSample textureSample = new TextureSample(name, inputStream, params);
        if (resourceCache != null) {
            if (resourceCache.checkObjectInCache(name)) {
                return (TextureSample) resourceCache.getCachedObject(name);
            }
            if (textureSample.isValid()) {
                resourceCache.addObjectInBuffer(name, textureSample);
            } else {
                throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
            }
        }
        return textureSample;
    }

    private ByteBuffer readTextureFromMemory(String name, InputStream inputStream) throws JGemsIOException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            byte[] stream = ByteStreams.toByteArray(inputStream);
            ByteBuffer buffer = MemoryUtil.memAlloc(stream.length);
            buffer.put(stream);
            buffer.flip();

            ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, width, height, channels, STBImage.STBI_rgb_alpha);
            MemoryUtil.memFree(buffer);
            if (imageBuffer == null) {
                throw new JGemsIOException("Couldn't create texture " + name + ". \n" + STBImage.stbi_failure_reason());
            } else {
                this.size = new Vector2i(width.get(), height.get());
                return imageBuffer;
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    private void setTextureParams(Params params) {
        int quality = params.isQualityAffected() ? (2 - JGems3D.get().getGameSettings().texturesQuality.getValue()) : 0;
        boolean linear = params.isLinearFilter() && JGems3D.get().getGameSettings().texturesFiltering.getValue() == 1;
        boolean anisotropic = params.isAnisotropicFilter() && JGems3D.get().getGameSettings().anisotropic.getValue() == 1;

        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, linear ? GL30.GL_LINEAR_MIPMAP_LINEAR : GL30.GL_NEAREST_MIPMAP_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, linear ? GL30.GL_LINEAR : GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, params.isRepeat() ? GL30.GL_REPEAT : GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, params.isRepeat() ? GL30.GL_REPEAT : GL30.GL_CLAMP_TO_EDGE);
        if (anisotropic) {
            GL30.glTexParameterf(GL30.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GL30.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        }
        GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_BASE_LEVEL, quality);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 11);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
    }

    private void registerTexture(ByteBuffer buffer, Params params) {
        if (buffer == null) {
            throw new JGemsIOException("Couldn't create texture " + this.getName());
        }
        this.textureId = GL20.glGenTextures();
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
        GL20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
        GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, this.size().x, this.size().y, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, buffer);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        STBImage.stbi_image_free(buffer);
        this.setTextureParams(params);
        JGemsHelper.getLogger().log("Texture " + this.getName() + " successfully created!");
    }

    public void clear() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        GL30.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    public void reloadTexture() {
        this.setTextureParams(this.params);
    }

    public void bindTexture() {
        if (!this.isValid()) {
            throw new JGemsRuntimeException("Tried to bind invalid texture");
        }
        GL30.glBindTexture(this.getTextureAttachment(), this.getTextureId());
    }

    @Override
    public Vector2i size() {
        return new Vector2i(this.size);
    }

    public String getName() {
        return this.name;
    }

    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureAttachment() {
        return GL30.GL_TEXTURE_2D;
    }

    public boolean isValid() {
        return this.getTextureId() > 0;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clear();
    }

    public static class Params {
        private final boolean linearFilter;
        private final boolean repeat;
        private final boolean anisotropicFilter;
        private final boolean qualityAffected;

        public Params(boolean qualityAffected) {
            this(true, true, true, qualityAffected);
        }

        public Params() {
            this(true, true, true, false);
        }

        public Params(boolean linearFilter, boolean repeat, boolean anisotropicFilter, boolean qualityAffected) {
            this.linearFilter = linearFilter;
            this.repeat = repeat;
            this.anisotropicFilter = anisotropicFilter;
            this.qualityAffected = qualityAffected;
        }

        public boolean isLinearFilter() {
            return this.linearFilter;
        }

        public boolean isRepeat() {
            return this.repeat;
        }

        public boolean isAnisotropicFilter() {
            return this.anisotropicFilter;
        }

        public boolean isQualityAffected() {
            return this.qualityAffected;
        }
    }
}