package ru.BouH.engine.render.scene.programs;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.render.scene.Scene;

public class CubeMapProgram {
    public static final String folder = "/textures/cubemaps/";

    private int textureId;

    public CubeMapProgram(String textureName, String format) {
        CubeMapTextureArray cubeMapTextureArray = new CubeMapTextureArray(textureName, format);
        this.generateTexture(cubeMapTextureArray);
    }

    public CubeMapProgram(CubeMapTextureArray cubeMapTextureArray) {
        this.generateTexture(cubeMapTextureArray);
    }

    private void generateTexture(CubeMapTextureArray cubeMapTextureArray) {
        this.textureId = GL30.glGenTextures();

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, this.textureId);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, GL30.GL_CLAMP_TO_EDGE);

        for (int i = 0; i < 6; i++) {
            TextureSample textureSample = cubeMapTextureArray.getTextureArray()[i];
            GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL30.GL_RGB16, textureSample.getWidth(), textureSample.getHeight(), 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, textureSample.getImageBuffer());
        }

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }

    public void bindCubeMap(int code) {
        Scene.activeGlTexture(code);
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
                this.textures[i] = ResourceManager.createTexture(builder.toString());
            }
        }

        public TextureSample[] getTextureArray() {
            return this.textures;
        }
    }
}
