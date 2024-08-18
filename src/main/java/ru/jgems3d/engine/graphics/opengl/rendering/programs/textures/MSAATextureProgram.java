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

package ru.jgems3d.engine.graphics.opengl.rendering.programs.textures;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

public class MSAATextureProgram implements ITextureProgram {
    private int textureId;
    private final int msaa;

    public MSAATextureProgram(int msaa) {
        this.msaa = msaa;
        this.textureId = GL30.glGenTextures();
    }

    public void createTexture(Vector2i size, int internalFormat) {
        this.bindTexture(GL43.GL_TEXTURE_2D_MULTISAMPLE);
        GL43.glTexImage2DMultisample(GL43.GL_TEXTURE_2D_MULTISAMPLE, this.msaa, internalFormat, size.x, size.y, true);
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
