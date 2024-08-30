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

package ru.jgems3d.toolbox.resources.samples;

import com.google.common.io.ByteStreams;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.ITextureSample;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.engine.system.service.exceptions.JGemsIOException;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.logger.SystemLogging;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureSample implements ITextureSample {
    private final String name;
    private int width;
    private int height;
    private int textureId;

    private TextureSample(String name, int width, int height, ByteBuffer buffer) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.createTexture(buffer);
    }

    private TextureSample(String fullPath) {
        this.name = fullPath;
        SystemLogging.get().getLogManager().log("Loading " + this.getName());
        try (InputStream inputStream = JGems3D.loadFileFromJar(new JGemsPath(fullPath))) {
            this.createTexture(this.readTextureFromMemory(this.getName(), inputStream));
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    private TextureSample(String id, InputStream inputStream) {
        this.name = id + "_inputStream";
        this.createTexture(this.readTextureFromMemory(id, inputStream));
    }

    public static TextureSample createTexture(ResourceCache resourceCache, String fullPath) {
        if (resourceCache.checkObjectInCache(fullPath)) {
            return (TextureSample) resourceCache.getCachedObject(fullPath);
        }
        TextureSample textureSample = new TextureSample(fullPath);
        if (textureSample.isValid()) {
            resourceCache.addObjectInBuffer(fullPath, textureSample);
        } else {
            throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    public static TextureSample createTexture(ResourceCache resourceCache, String name, int width, int height, ByteBuffer buffer) {
        if (resourceCache.checkObjectInCache(name)) {
            return (TextureSample) resourceCache.getCachedObject(name);
        }
        TextureSample textureSample = new TextureSample(name, width, height, buffer);
        if (textureSample.isValid()) {
            resourceCache.addObjectInBuffer(name, textureSample);
        } else {
            throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
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
                this.width = width.get();
                this.height = height.get();
                return imageBuffer;
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    private void createTexture(ByteBuffer buffer) {
        this.textureId = GL20.glGenTextures();
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
        GL20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
        GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, this.size().x, this.size().y, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, buffer);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_BASE_LEVEL, 0);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 11);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
        GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        STBImage.stbi_image_free(buffer);
        SystemLogging.get().getLogManager().log("Texture " + this.getName() + " successfully created!");
    }

    public void clear() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        GL30.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    public void bindTexture() {
        if (!this.isValid()) {
            throw new JGemsRuntimeException("Tried to bind invalid texture");
        }
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.getTextureId());
    }

    @Override
    public Vector2i size() {
        return new Vector2i(this.width, this.height);
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
        return this.getTextureId() != 0;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clear();
    }
}
