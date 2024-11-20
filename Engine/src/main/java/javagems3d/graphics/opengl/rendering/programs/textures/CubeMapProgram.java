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

package javagems3d.graphics.opengl.rendering.programs.textures;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class CubeMapProgram {
    private int textureId;

    public CubeMapProgram() {
    }

    public void createCubeMap(Vector2i size, int internalFormat, int textureFormat, int filter, int clamp) {
        this.textureId = GL46.glGenTextures();

        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, this.textureId);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MIN_FILTER, filter);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MAG_FILTER, filter);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_T, clamp);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_S, clamp);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_R, clamp);

        for (int i = 0; i < 6; i++) {
            GL46.glTexImage2D(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, internalFormat, size.x, size.y, 0, textureFormat, GL46.GL_FLOAT, (ByteBuffer) null);
        }

        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);
    }

    public boolean isValid() {
        return this.textureId > 0;
    }

    public void unBindCubeMap() {
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);
    }

    public void bindCubeMap() {
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, this.getTextureId());
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void cleanCubeMap() {
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);
        GL46.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }
}