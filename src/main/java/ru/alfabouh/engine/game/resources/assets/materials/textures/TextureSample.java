package ru.alfabouh.engine.game.resources.assets.materials.textures;

import com.google.common.io.ByteStreams;
import org.joml.Vector2d;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;
import ru.alfabouh.engine.game.resources.cache.GameCache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureSample implements IImageSample {
    private ByteBuffer imageBuffer;
    private int width;
    private int height;
    private int textureId;
    private final String name;
    private final boolean interpolate;
    private final int wrapping;
    private boolean enableAnisotropic;

    private TextureSample(String name, int width, int height, ByteBuffer buffer) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.imageBuffer = buffer;

        this.wrapping = GL30.GL_CLAMP_TO_BORDER;
        this.enableAnisotropic = false;
        this.interpolate = false;

        if (this.imageBuffer != null) {
            this.createTexture();
        }
    }

    private TextureSample(boolean inJar, String fullPath, boolean interpolate, int wrapping) {
        this.name = fullPath;
        this.interpolate = interpolate;
        this.wrapping = wrapping;
        this.enableAnisotropic = true;
        Game.getGame().getLogManager().log("Loading " + this.getName());
        if (inJar) {
            try (InputStream inputStream = Game.loadFileJar(this.getName())) {
                this.imageBuffer = this.readTextureFromMemory(this.getName(), inputStream);
                if (this.imageBuffer != null) {
                    this.createTexture();
                }
            } catch (IOException e) {
                throw new GameException(e);
            }
        } else {
            this.imageBuffer = this.readTextureOutsideJar(this.getName());
            if (this.imageBuffer != null) {
                this.createTexture();
            }
        }
    }

    private TextureSample(String id, InputStream inputStream, boolean interpolate, int wrapping) {
        this.name = id + "_inputStream";
        this.interpolate = interpolate;
        this.wrapping = wrapping;
        this.enableAnisotropic = true;
        if (inputStream == null) {
            Game.getGame().getLogManager().warn("Error, while loading texture " + id + " InputStream is NULL");
            this.imageBuffer = null;
        } else {
            this.imageBuffer = this.readTextureFromMemory(id, inputStream);
            if (this.imageBuffer != null) {
                this.createTexture();
            }
        }
    }

    public static TextureSample createTextureOutsideJar(GameCache gameCache, String fullPath, boolean interpolate, int wrapping) {
        if (gameCache.checkObjectInCache(fullPath)) {
            return gameCache.getCachedTexture(fullPath);
        }
        TextureSample textureSample = new TextureSample(false, fullPath, interpolate, wrapping);
        if (textureSample.isValid()) {
            gameCache.addObjectInBuffer(fullPath, textureSample);
        } else {
            throw new GameException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    public static TextureSample createTexture(GameCache gameCache, String fullPath, boolean interpolate, int wrapping) {
        if (gameCache.checkObjectInCache(fullPath)) {
            return gameCache.getCachedTexture(fullPath);
        }
        TextureSample textureSample = new TextureSample(true, fullPath, interpolate, wrapping);
        if (textureSample.isValid()) {
            gameCache.addObjectInBuffer(fullPath, textureSample);
        } else {
            throw new GameException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
    }

    public static TextureSample createTexture(GameCache gameCache, String name, int width, int height, ByteBuffer buffer) {
        if (gameCache.checkObjectInCache(name)) {
            return gameCache.getCachedTexture(name);
        }
        TextureSample textureSample = new TextureSample(name, width, height, buffer);
        if (textureSample.isValid()) {
            gameCache.addObjectInBuffer(name, textureSample);
        } else {
            throw new GameException("Couldn't add invalid texture in cache!");
        }
        return textureSample;
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
            } else {
                this.width = width.get();
                this.height = height.get();
                return imageBuffer;
            }
        } catch (IOException e) {
            throw new GameException(e);
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
            } else {
                this.width = width.get();
                this.height = height.get();
                return imageBuffer;
            }
        }
        return null;
    }

    private void createTexture() {
        boolean linear = Game.getGame().getGameSettings().texturesFiltering.getValue() == 1;
        boolean anisotropic = Game.getGame().getGameSettings().anisotropic.getValue() == 1;

        this.textureId = GL20.glGenTextures();
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.getTextureId());
        GL20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
        GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, this.getWidth(), this.getHeight(), 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, this.getImageBuffer());
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, (linear && this.isInterpolate()) ? GL30.GL_LINEAR_MIPMAP_LINEAR : GL30.GL_NEAREST_MIPMAP_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, (linear && this.isInterpolate()) ? GL30.GL_LINEAR : GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_BASE_LEVEL, 0);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 6);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, this.getWrapping());
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, this.getWrapping());
        if (anisotropic && this.isEnableAnisotropic()) {
            GL30.glTexParameterf(GL30.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GL30.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        }
        GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        Game.getGame().getLogManager().log("Texture " + this.getName() + " successfully created!");
    }

    public static TextureSample createTextureIS(String id, InputStream inputStream, boolean interpolate, int wrapping) {
        return new TextureSample(id, inputStream, interpolate, wrapping);
    }

    public void recreateTexture() {
        GL30.glDeleteTextures(this.getTextureId());
        this.createTexture();
    }

    public void clear() {
        if (this.isValid()) {
            STBImage.stbi_image_free(this.getImageBuffer());
            this.imageBuffer = null;
        }
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        GL30.glDeleteTextures(this.getTextureId());
    }

    public void bindTexture() {
        if (!this.isValid()) {
            throw new GameException("Tried to bind invalid texture");
        }
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.getTextureId());
    }

    public void setEnableAnisotropic(boolean enableAnisotropic) {
        this.enableAnisotropic = enableAnisotropic;
    }

    public boolean isEnableAnisotropic() {
        return this.enableAnisotropic;
    }

    public int getWrapping() {
        return this.wrapping;
    }

    public boolean isInterpolate() {
        return this.interpolate;
    }

    public String getName() {
        return this.name;
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

    public boolean isValid() {
        return this.getImageBuffer() != null;
    }

    public ByteBuffer getImageBuffer() {
        return this.imageBuffer;
    }

    @Override
    public void onCleaningCache(GameCache gameCache) {
        this.clear();
    }
}