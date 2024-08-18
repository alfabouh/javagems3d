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

package ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL43;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.attachments.T2DAttachment;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.ITextureProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.MSAATextureProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.TextureProgram;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FBOTexture2DProgram {
    private final List<ITextureProgram> texturePrograms;
    private final boolean drawColor;
    private int frameBufferId;
    private int renderBufferId;

    public FBOTexture2DProgram(boolean drawColor) {
        this.texturePrograms = new ArrayList<>();
        this.drawColor = drawColor;
    }

    public void createFrameBuffer2DTextureMSAA(Vector2i size, int[] attachments, int internalFormat, int msaa) {
        this.frameBufferId = GL30.glGenFramebuffers();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        for (int attachment : attachments) {
            MSAATextureProgram msaaTextureProgram = new MSAATextureProgram(msaa);
            msaaTextureProgram.createTexture(size, internalFormat);
            GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL43.GL_TEXTURE_2D_MULTISAMPLE, ((ITextureProgram) msaaTextureProgram).getTextureId(), 0);
            this.getTexturePrograms().add(msaaTextureProgram);
        }

        if (!this.drawColor) {
            GL30.glDrawBuffer(GL30.GL_NONE);
            GL30.glReadBuffer(GL30.GL_NONE);
        } else {
            GL30.glDrawBuffers(Arrays.stream(attachments).distinct().toArray());
        }

        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.renderBufferId);
        GL43.glRenderbufferStorageMultisample(GL43.GL_RENDERBUFFER, msaa, GL30.GL_DEPTH24_STENCIL8, size.x, size.y);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, this.renderBufferId);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            int errCode = GL43.glGetError();
            throw new JGemsRuntimeException("Failed to create framebuffer: " + Integer.toHexString(errCode));
        }

        this.unBindFBO();
    }

    public void createFrameBuffer2DTexture(Vector2i size, T2DAttachmentContainer t2DAttachmentContainer, boolean depthBuffer, int filtering, int compareMode, int compareFunc, int clamp, float[] borderColor) {
        if (size.x <= 0.0f || size.y <= 0.0f) {
            return;
        }
        this.frameBufferId = GL30.glGenFramebuffers();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        for (T2DAttachment t2DAttachment1 : t2DAttachmentContainer.getT2DAttachmentSet()) {
            TextureProgram textureProgram1 = new TextureProgram();
            textureProgram1.createTexture(size, t2DAttachment1.getTextureFormat(), t2DAttachment1.getInternalFormat(), filtering, filtering, compareMode, compareFunc, clamp, clamp, borderColor);
            GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, t2DAttachment1.getAttachment(), GL43.GL_TEXTURE_2D, ((ITextureProgram) textureProgram1).getTextureId(), 0);
            this.getTexturePrograms().add(textureProgram1);
        }

        if (!this.drawColor) {
            GL30.glDrawBuffer(GL30.GL_NONE);
            GL30.glReadBuffer(GL30.GL_NONE);
        } else {
            GL30.glDrawBuffers(t2DAttachmentContainer.getT2DAttachmentSet().stream().map(T2DAttachment::getAttachment).distinct().mapToInt(Integer::intValue).toArray());
        }

        if (depthBuffer) {
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.renderBufferId);
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, size.x, size.y);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, this.renderBufferId);
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        }

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            int errCode = GL43.glGetError();
            throw new JGemsRuntimeException("Failed to create framebuffer: " + Integer.toHexString(errCode));
        }

        this.unBindFBO();
    }

    public void copyFBOtoFBOColor(int fboTo, int[] attachmentsToCopy, Vector2i dimension) {
        GL43.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.getFrameBufferId());
        GL43.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fboTo);
        for (int att : attachmentsToCopy) {
            GL43.glReadBuffer(att);
            GL43.glDrawBuffer(att);
            GL43.glBlitFramebuffer(0, 0, dimension.x, dimension.y, 0, 0, dimension.x, dimension.y, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);
        }
        GL43.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void copyFBOtoFBODepth(int fboTo, Vector2i dimension) {
        GL43.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.getFrameBufferId());
        GL43.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fboTo);
        GL43.glBlitFramebuffer(0, 0, dimension.x, dimension.y, 0, 0, dimension.x, dimension.y, GL30.GL_DEPTH_BUFFER_BIT, GL30.GL_NEAREST);
        GL43.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void connectTextureToBuffer(int attachment, int i) {
        GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_TEXTURE_2D, this.getTexturePrograms().get(i).getTextureId(), 0);
    }

    public List<ITextureProgram> getTexturePrograms() {
        return this.texturePrograms;
    }

    public int getRenderBufferId() {
        return this.renderBufferId;
    }

    public int getFrameBufferId() {
        return this.frameBufferId;
    }

    public void bindFBO() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBufferId);
    }

    public void unBindFBO() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public int getTextureIDByIndex(int i) {
        return this.getTexturePrograms().get(i).getTextureId();
    }

    public void bindTexture(int i) {
        this.getTexturePrograms().get(i).bindTexture(GL30.GL_TEXTURE_2D);
    }

    public void unBindTexture() {
        this.getTexturePrograms().get(0).unBindTexture();
    }

    public boolean isValid() {
        return this.frameBufferId > 0;
    }

    public void clearFBO() {
        if (!this.isValid()) {
            return;
        }
        for (ITextureProgram textureProgram : this.getTexturePrograms()) {
            textureProgram.cleanUp();
        }
        this.getTexturePrograms().clear();
        GL30.glDeleteRenderbuffers(this.renderBufferId);
        GL30.glDeleteFramebuffers(this.frameBufferId);
        this.frameBufferId = -1;
    }
}
