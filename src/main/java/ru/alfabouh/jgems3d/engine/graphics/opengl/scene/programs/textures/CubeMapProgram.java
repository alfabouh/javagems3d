package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.programs.textures;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;

import java.nio.ByteBuffer;

public class CubeMapProgram {
    public static final String folder = "/assets/jgems/textures/cubemaps/";

    private int textureId;

    public CubeMapProgram() {
    }

    public void createCubeMap(Vector2i size, int internalFormat, int textureFormat, int filter, int clamp) {
        this.textureId = GL30.glGenTextures();

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, this.textureId);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, filter);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, filter);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, clamp);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, clamp);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, clamp);

        for (int i = 0; i < 6; i++) {
            GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, internalFormat, size.x, size.y, 0, textureFormat, GL30.GL_FLOAT, (ByteBuffer) null);
        }

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }

    public void generateCubeMapFromTexture(CubeMapTextureArray cubeMapTextureArray) {
        this.textureId = GL30.glGenTextures();

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, this.textureId);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, GL30.GL_CLAMP_TO_EDGE);

        for (int i = 0; i < 6; i++) {
            TextureSample textureSample = cubeMapTextureArray.getTextureArray()[i];
            GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL30.GL_RGB16, textureSample.size().x, textureSample.size().y, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, textureSample.getImageBuffer());
        }

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }

    public void unBindCubeMap() {
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }

    public void bindCubeMap() {
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, this.getTextureId());
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void cleanCubeMap() {
        GL30.glDeleteTextures(this.getTextureId());
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }

    public static class CubeMapTextureArray {
        private final TextureSample[] textures;

        @SuppressWarnings("all")
        public CubeMapTextureArray(String name, String format) {
            this.textures = new TextureSample[6];
            for (int i = 0; i < 6; i++) {
                StringBuilder builder = new StringBuilder();
                builder.append(CubeMapProgram.folder);
                builder.append(name);
                builder.append("_");
                builder.append(i + 1);
                builder.append(format);
                this.textures[i] = JGemsResourceManager.getGlobalGameResources().createTexture(builder.toString(), true, GL30.GL_REPEAT);
            }
        }

        public TextureSample[] getTextureArray() {
            return this.textures;
        }
    }
}