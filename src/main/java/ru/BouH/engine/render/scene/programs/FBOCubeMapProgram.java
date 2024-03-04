package ru.BouH.engine.render.scene.programs;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.screen.Screen;

public class FBOCubeMapProgram {
    private final CubeMapProgram cubeMapProgram;
    private final boolean drawColor;
    private final boolean aliasing;
    private int frameBufferId;
    private int renderBufferId;

    public FBOCubeMapProgram(boolean drawColor, boolean aliasing) {
        this.cubeMapProgram = new CubeMapProgram();
        this.drawColor = drawColor;
        this.aliasing = aliasing;
    }

    public void createFrameBufferCubeMap(Vector2i size, boolean depthBuffer, int attachment, int internalFormat, int textureFormat, int filtering, int clamp) {
        if (!Scene.isSceneActive()) {
            return;
        }
        this.frameBufferId = GL30.glGenFramebuffers();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        this.getCubeMapProgram().createCubeMap(size, internalFormat, textureFormat, filtering, clamp, this.aliasing);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, attachment, this.getCubeMapProgram().getTextureId(), 0);

        if (!this.drawColor) {
            GL30.glDrawBuffer(GL30.GL_NONE);
            GL30.glReadBuffer(GL30.GL_NONE);
        } else {
            GL30.glDrawBuffer(attachment);
        }

        if (depthBuffer) {
            this.bindRenderDepthFBO();
            if (this.msaaSamples() > 0) {
                GL43.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, this.msaaSamples(), GL30.GL_DEPTH24_STENCIL8, size.x, size.y);
            } else {
                GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, size.x, size.y);
            }
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, this.renderBufferId);
            this.unBindRenderDepthFBO();
        }

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new GameException("Failed to create framebuffer!");
        }

        this.unBindFBO();
    }

    public void connectCubeMapToBuffer(int attachment, int j) {
        GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + j, this.getCubeMapProgram().getTextureId(), 0);
    }

    public int msaaSamples() {
        return Screen.MSAA_SAMPLES;
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
