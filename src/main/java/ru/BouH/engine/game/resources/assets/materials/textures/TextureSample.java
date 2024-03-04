package ru.BouH.engine.game.resources.assets.materials.textures;

import com.google.common.io.ByteStreams;
import org.joml.Vector2d;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.resources.cache.GameCache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureSample implements IImageSample {
    private final ByteBuffer imageBuffer;
    private final Vector2d scaling;
    private int width;
    private int height;
    private int textureId;
    private boolean isValid;

    private TextureSample(boolean inJar, String fullPath) {
        this.isValid = true;
        this.scaling = new Vector2d(1.0d);
        Game.getGame().getLogManager().debug("Loading " + fullPath);
        if (inJar) {
            try (InputStream inputStream = Game.loadFileJar(fullPath)) {
                if (inputStream == null) {
                    Game.getGame().getLogManager().warn("Error, while loading texture " + fullPath + " InputStream is NULL");
                    this.imageBuffer = null;
                } else {
                    this.imageBuffer = this.readTextureFromMemory(fullPath, inputStream);
                    if (this.imageBuffer != null) {
                        this.createTexture(fullPath, this.imageBuffer);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.imageBuffer = this.readTextureOutsideJar(fullPath);
            if (this.imageBuffer != null) {
                this.createTexture(fullPath, this.imageBuffer);
            }
        }
    }

    private TextureSample(String id, InputStream inputStream) {
        this.isValid = true;
        this.scaling = new Vector2d(1.0d);
        if (inputStream == null) {
            Game.getGame().getLogManager().warn("Error, while loading texture " + id + " InputStream is NULL");
            this.imageBuffer = null;
        } else {
            this.imageBuffer = this.readTextureFromMemory(id, inputStream);
            if (this.imageBuffer != null) {
                this.createTexture(id, this.imageBuffer);
            }
        }
    }

    public static TextureSample createTextureOutsideJar(GameCache gameCache, String fullPath) {
        if (gameCache.checkObjectInCache(fullPath)) {
            return gameCache.getCachedTexture(fullPath);
        }
        TextureSample textureSample = new TextureSample(false, fullPath);
        if (textureSample.isValid()) {
            gameCache.addObjectInBuffer(fullPath, textureSample);
        }
        return textureSample;
    }

    public static TextureSample createTexture(GameCache gameCache, String fullPath) {
        if (gameCache.checkObjectInCache(fullPath)) {
            return gameCache.getCachedTexture(fullPath);
        }
        TextureSample textureSample = new TextureSample(true, fullPath);
        if (textureSample.isValid()) {
            gameCache.addObjectInBuffer(fullPath, textureSample);
        }
        return textureSample;
    }

    public static TextureSample createTextureIS(String id, InputStream inputStream) {
        return new TextureSample(id, inputStream);
    }

    private ByteBuffer readTextureFromMemory(String name, InputStream inputStream) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            byte[] stream = ByteStreams.toByteArray(inputStream);
            ByteBuffer buffer = MemoryUtil.memAlloc(stream.length);
            buffer.put(stream);
            buffer.flip();

            ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, width, height, channels, STBImage.STBI_rgb_alpha);
            if (imageBuffer == null) {
                Game.getGame().getLogManager().warn("Couldn't create texture " + name);
                Game.getGame().getLogManager().bigWarn(STBImage.stbi_failure_reason());
                this.isValid = false;
            } else {
                this.width = width.get();
                this.height = height.get();
                return imageBuffer;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private ByteBuffer readTextureOutsideJar(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer imageBuffer = STBImage.stbi_load(path, width, height, channels, STBImage.STBI_rgb_alpha);
            if (imageBuffer == null) {
                Game.getGame().getLogManager().warn("Couldn't create texture " + path);
                Game.getGame().getLogManager().bigWarn(STBImage.stbi_failure_reason());
                this.isValid = false;
            } else {
                this.width = width.get();
                this.height = height.get();
                return imageBuffer;
            }
        }
        return null;
    }

    private void createTexture(String name, ByteBuffer buffer) {
        this.textureId = GL20.glGenTextures();
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
        GL20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
        GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, this.getWidth(), this.getHeight(), 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, this.getImageBuffer());
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 11);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
        GL30.glTexParameterf(GL30.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GL30.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        Game.getGame().getLogManager().log("Texture " + name + " successfully created!");
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void clear() {
        if (this.isValid()) {
            STBImage.stbi_image_free(this.getImageBuffer());
        }
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        GL30.glDeleteTextures(this.getTextureId());
        this.isValid = false;
    }

    public boolean isValid() {
        return this.isValid && this.getImageBuffer() != null;
    }

    public ByteBuffer getImageBuffer() {
        return this.imageBuffer;
    }

    public void setScaling(Vector2d vector2d) {
        this.scaling.set(vector2d);
    }

    @Override
    public Vector2d scaling() {
        return new Vector2d(this.scaling);
    }

    public void bindTexture() {
        if (!this.isValid()) {
            throw new GameException("Tried to bind invalid texture");
        }
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.getTextureId());
    }

    @Override
    public void onCleaningCache(GameCache gameCache) {
        this.clear();
    }
}