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

package javagems3d.graphics.rendering.programs.fbo;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.programs.textures.CubeMapTextureProgram;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

public class FBOCubeMapProgram {
    private final CubeMapTextureProgram cubeMapTextureProgram;
    private int frameBufferId;
    private int renderBufferId;

    public FBOCubeMapProgram() {
        this.cubeMapTextureProgram = new CubeMapTextureProgram();
    }

    public void createFrameBufferCubeMapDepth(Vector2i size, int filtering, int clamp) {
        this.frameBufferId = GL46.glGenFramebuffers();
        this.renderBufferId = GL46.glGenRenderbuffers();
        this.bindFBO();

        this.getCubeMapProgram().createCubeMap(size, GL46.GL_DEPTH_COMPONENT, GL46.GL_DEPTH_COMPONENT, filtering, clamp);
        GL46.glFramebufferTexture(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_ATTACHMENT, this.getCubeMapProgram().getTextureId(), 0);

        GL46.glDrawBuffer(GL46.GL_NONE);
        GL46.glReadBuffer(GL46.GL_NONE);

        if (GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER) != GL46.GL_FRAMEBUFFER_COMPLETE) {
            throw new JGemsRuntimeException("Failed to create framebuffer!");
        }

        this.unBindFBO();
    }

    public void createFrameBufferCubeMapColor(Vector2i size, boolean depthBuffer, int internalFormat, int textureFormat, int filtering, int clamp) {
        this.frameBufferId = GL46.glGenFramebuffers();
        this.renderBufferId = GL46.glGenRenderbuffers();
        this.bindFBO();

        this.getCubeMapProgram().createCubeMap(size, internalFormat, textureFormat, filtering, clamp);
        for (int i = 0; i < 6; i++) {
            GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0, GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, this.getCubeMapProgram().getTextureId(), 0);
        }
        GL46.glDrawBuffers(new int[]{GL46.GL_COLOR_ATTACHMENT0});

        if (depthBuffer) {
            GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, this.renderBufferId);
            GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH24_STENCIL8, size.x, size.y);
            GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_STENCIL_ATTACHMENT, GL46.GL_RENDERBUFFER, this.renderBufferId);
            GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, 0);
        }

        if (GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER) != GL46.GL_FRAMEBUFFER_COMPLETE) {
            throw new JGemsRuntimeException("Failed to create framebuffer!");
        }

        this.unBindFBO();
    }

    public void connectCubeMapToBuffer(int attachment, int j) {
        GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, attachment, GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + j, this.getCubeMapProgram().getTextureId(), 0);
    }

    public CubeMapTextureProgram getCubeMapProgram() {
        return this.cubeMapTextureProgram;
    }

    public int getRenderBufferId() {
        return this.renderBufferId;
    }

    public int getFrameBufferId() {
        return this.frameBufferId;
    }

    public void bindRenderDepthFBO() {
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, this.renderBufferId);
    }

    public void unBindRenderDepthFBO() {
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, 0);
    }

    public void bindFBO() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, this.frameBufferId);
    }

    public void unBindFBO() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
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
        this.getCubeMapProgram().clearCubeMap();
        GL46.glDeleteRenderbuffers(this.renderBufferId);
        GL46.glDeleteFramebuffers(this.frameBufferId);
    }
}
