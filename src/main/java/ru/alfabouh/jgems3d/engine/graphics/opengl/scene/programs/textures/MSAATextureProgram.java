package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.programs.textures;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

public class MSAATextureProgram implements ITextureProgram {
    private final int textureId;
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

    public int getTextureId() {
        return this.textureId;
    }
}
