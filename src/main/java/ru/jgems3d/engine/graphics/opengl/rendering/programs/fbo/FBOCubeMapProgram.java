package ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.CubeMapProgram;
import ru.jgems3d.exceptions.JGemsException;

public class FBOCubeMapProgram {
    private final CubeMapProgram cubeMapProgram;
    private int frameBufferId;
    private int renderBufferId;

    public FBOCubeMapProgram() {
        this.cubeMapProgram = new CubeMapProgram();
    }

    public void createFrameBufferCubeMapDepth(Vector2i size, int filtering, int clamp) {
        this.frameBufferId = GL30.glGenFramebuffers();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        this.getCubeMapProgram().createCubeMap(size, GL30.GL_DEPTH_COMPONENT, GL30.GL_DEPTH_COMPONENT, filtering, clamp);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, this.getCubeMapProgram().getTextureId(), 0);

        GL30.glDrawBuffer(GL30.GL_NONE);
        GL30.glReadBuffer(GL30.GL_NONE);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new JGemsException("Failed to create framebuffer!");
        }

        this.unBindFBO();
    }

    public void createFrameBufferCubeMapColor(Vector2i size, boolean depthBuffer, int internalFormat, int textureFormat, int filtering, int clamp) {
        this.frameBufferId = GL30.glGenFramebuffers();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        this.getCubeMapProgram().createCubeMap(size, internalFormat, textureFormat, filtering, clamp);
        for (int i = 0; i < 6; i++) {
            GL32.glFramebufferTexture2D(GL32.GL_FRAMEBUFFER, GL32.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, this.getCubeMapProgram().getTextureId(), 0);
        }
        GL30.glDrawBuffers(new int[]{GL30.GL_COLOR_ATTACHMENT0});

        if (depthBuffer) {
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.renderBufferId);
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, size.x, size.y);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, this.renderBufferId);
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        }

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new JGemsException("Failed to create framebuffer!");
        }

        this.unBindFBO();
    }

    public void connectCubeMapToBuffer(int attachment, int j) {
        GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + j, this.getCubeMapProgram().getTextureId(), 0);
    }

    public CubeMapProgram getCubeMapProgram() {
        return this.cubeMapProgram;
    }

    public int getRenderBufferId() {
        return this.renderBufferId;
    }

    public int getFrameBufferId() {
        return this.frameBufferId;
    }

    public void bindRenderDepthFBO() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.renderBufferId);
    }

    public void unBindRenderDepthFBO() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
    }

    public void bindFBO() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBufferId);
    }

    public void unBindFBO() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void bindCubeMap() {
        this.getCubeMapProgram().bindCubeMap();
    }

    public void unBindCubeMap() {
        this.getCubeMapProgram().unBindCubeMap();
    }

    public void clearFBO() {
        this.unBindFBO();
        this.unBindRenderDepthFBO();
        this.getCubeMapProgram().cleanCubeMap();
        GL30.glDeleteRenderbuffers(this.renderBufferId);
        GL30.glDeleteFramebuffers(this.frameBufferId);
    }
}
