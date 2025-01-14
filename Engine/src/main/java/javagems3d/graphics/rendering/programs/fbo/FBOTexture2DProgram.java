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

import javagems3d.system.service.collections.Pair;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachment;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.graphics.rendering.programs.textures.Texture2DMSAAProgram;
import javagems3d.graphics.rendering.programs.textures.Texture2DProgram;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

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
        this.frameBufferId = GL46.glGenFramebuffers();
        this.renderBufferId = GL46.glGenRenderbuffers();
        this.bindFBO();

        for (int attachment : attachments) {
            Texture2DMSAAProgram texture2DMSAAProgram = new Texture2DMSAAProgram(msaa);
            texture2DMSAAProgram.createTexture(size, new Texture2DMSAAProgram.Properties(internalFormat, GL46.GL_LINEAR, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_NONE, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, null), null);
            GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, attachment, GL46.GL_TEXTURE_2D_MULTISAMPLE, ((ITextureProgram) texture2DMSAAProgram).getTextureId(), 0);
            this.getTexturePrograms().add(texture2DMSAAProgram);
        }

        if (!this.drawColor) {
            GL46.glDrawBuffer(GL46.GL_NONE);
            GL46.glReadBuffer(GL46.GL_NONE);
        } else {
            GL46.glDrawBuffers(Arrays.stream(attachments).distinct().toArray());
        }

        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, this.renderBufferId);
        GL46.glRenderbufferStorageMultisample(GL46.GL_RENDERBUFFER, msaa, GL46.GL_DEPTH24_STENCIL8, size.x, size.y);
        GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_STENCIL_ATTACHMENT, GL46.GL_RENDERBUFFER, this.renderBufferId);
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, 0);

        if (GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER) != GL46.GL_FRAMEBUFFER_COMPLETE) {
            int errCode = GL46.glGetError();
            throw new JGemsRuntimeException("Failed to create framebuffer: " + Integer.toHexString(errCode));
        }

        this.unBindFBO();
    }

    public void createFrameBuffer2DTexture(Vector2i size, T2DAttachmentContainer t2DAttachmentContainer, boolean depthBuffer, int filtering, int compareMode, int compareFunc, int clamp, float[] borderColor) {
        if (size.x <= 0.0f || size.y <= 0.0f) {
            return;
        }
        this.frameBufferId = GL46.glGenFramebuffers();
        this.renderBufferId = GL46.glGenRenderbuffers();
        this.bindFBO();

        for (T2DAttachment t2DAttachment1 : t2DAttachmentContainer.getT2DAttachmentSet()) {
            Texture2DProgram texture2DProgram1 = new Texture2DProgram();
            texture2DProgram1.createTexture(size, new Texture2DProgram.Properties(t2DAttachment1.getTextureFormat(), t2DAttachment1.getInternalFormat(), filtering, filtering, compareMode, compareFunc, clamp, clamp, borderColor),null);
            GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, t2DAttachment1.getAttachment(), GL46.GL_TEXTURE_2D, ((ITextureProgram) texture2DProgram1).getTextureId(), 0);
            this.getTexturePrograms().add(texture2DProgram1);
        }

        if (!this.drawColor) {
            GL46.glDrawBuffer(GL46.GL_NONE);
            GL46.glReadBuffer(GL46.GL_NONE);
        } else {
            GL46.glDrawBuffers(t2DAttachmentContainer.getT2DAttachmentSet().stream().map(T2DAttachment::getAttachment).distinct().mapToInt(Integer::intValue).toArray());
        }

        if (depthBuffer) {
            GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, this.renderBufferId);
            GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH24_STENCIL8, size.x, size.y);
            GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_STENCIL_ATTACHMENT, GL46.GL_RENDERBUFFER, this.renderBufferId);
            GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, 0);
        }

        if (GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER) != GL46.GL_FRAMEBUFFER_COMPLETE) {
            int errCode = GL46.glGetError();
            throw new JGemsRuntimeException("Failed to create framebuffer: " + Integer.toHexString(errCode));
        }

        this.unBindFBO();
    }

    public void copyFBOtoFBOColor(int fboTo, Pair<Integer, Integer>[] colorFrom_colorTo, Vector2i dimension) {
        GL46.glBindFramebuffer(GL46.GL_READ_FRAMEBUFFER, this.getFrameBufferId());
        GL46.glBindFramebuffer(GL46.GL_DRAW_FRAMEBUFFER, fboTo);
        for (Pair<Integer, Integer> att : colorFrom_colorTo) {
            GL46.glReadBuffer(att.getFirst());
            GL46.glDrawBuffer(att.getSecond());
            GL46.glBlitFramebuffer(0, 0, dimension.x, dimension.y, 0, 0, dimension.x, dimension.y, GL46.GL_COLOR_BUFFER_BIT, GL46.GL_NEAREST);
        }
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }

    public void copyFBOtoFBODepth(int fboTo, Vector2i dimension) {
        GL46.glBindFramebuffer(GL46.GL_READ_FRAMEBUFFER, this.getFrameBufferId());
        GL46.glBindFramebuffer(GL46.GL_DRAW_FRAMEBUFFER, fboTo);
        GL46.glBlitFramebuffer(0, 0, dimension.x, dimension.y, 0, 0, dimension.x, dimension.y, GL46.GL_DEPTH_BUFFER_BIT, GL46.GL_NEAREST);
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }

    public void connectTextureToBuffer(int attachment, int i) {
        GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, attachment, GL46.GL_TEXTURE_2D, this.getTexturePrograms().get(i).getTextureId(), 0);
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
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, this.frameBufferId);
    }

    public void unBindFBO() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }

    public int getTextureIDByIndex(int i) {
        return this.getTexturePrograms().get(i).getTextureId();
    }

    public ITextureProgram getTextureByIndex(int i) {
        return this.getTexturePrograms().get(i);
    }

    public void bindTexture(int i) {
        this.getTexturePrograms().get(i).bindTexture();
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
            textureProgram.clear();
        }
        this.getTexturePrograms().clear();
        GL46.glDeleteRenderbuffers(this.renderBufferId);
        GL46.glDeleteFramebuffers(this.frameBufferId);
        this.frameBufferId = -1;
    }
}
