package ru.BouH.engine.render.scene.programs;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class FBOTexture2DProgram {
    private final List<ITextureProgram> texturePrograms;
    private final boolean drawColor;
    private final boolean aliasing;
    private int frameBufferId;
    private int renderBufferId;

    public FBOTexture2DProgram(boolean drawColor, boolean aliasing) {
        this.texturePrograms = new ArrayList<>();
        this.drawColor = drawColor;
        this.aliasing = aliasing;
    }

    public void createFrameBuffer2DTexture(Vector2i size, int[] attachments, boolean depthBuffer, boolean enableMsaa, int internalFormat, int textureFormat, int filtering, int compareMode, int compareFunc, int clamp, float[] borderColor) {
        if (!Scene.isSceneActive()) {
            return;
        }
        this.frameBufferId = GL30.glGenFramebuffers();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        for (int attachment : attachments) {
            ITextureProgram textureProgram = null;
            if (enableMsaa) {
                MSAATextureProgram msaaTextureProgram = new MSAATextureProgram(Screen.MSAA_SAMPLES);
                textureProgram = msaaTextureProgram;
                msaaTextureProgram.createTexture(size, internalFormat, filtering, filtering, compareMode, compareFunc, clamp, clamp, borderColor, this.aliasing);
                GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL43.GL_TEXTURE_2D_MULTISAMPLE, textureProgram.getTextureId(), 0);
            } else {
                TextureProgram textureProgram1 = new TextureProgram();
                textureProgram = textureProgram1;
                textureProgram1.createTexture(size, internalFormat, textureFormat, filtering, filtering, compareMode, compareFunc, clamp, clamp, borderColor, this.aliasing);
                GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL43.GL_TEXTURE_2D, textureProgram.getTextureId(), 0);
            }
            this.getTexturePrograms().add(textureProgram);
        }

        if (!this.drawColor) {
            GL30.glDrawBuffer(GL30.GL_NONE);
            GL30.glReadBuffer(GL30.GL_NONE);
        } else {
            GL30.glDrawBuffers(attachments);
        }

        if (depthBuffer || enableMsaa) {
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.renderBufferId);
            if (enableMsaa) {
                GL43.glRenderbufferStorageMultisample(GL43.GL_RENDERBUFFER, Screen.MSAA_SAMPLES, GL30.GL_DEPTH24_STENCIL8, size.x, size.y);
            } else {
                GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, size.x, size.y);
            }
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, this.renderBufferId);
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        }

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            int errCode = GL43.glGetError();
            throw new GameException("Failed to create framebuffer: " + Integer.toHexString(errCode));
        }

        this.unBindFBO();
    }

    public void copyFBOtoFBO(int fboTo, int[] attachmentsToCopy, Vector2i dimension) {
        GL43.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.getFrameBufferId());
        GL43.glBindFramebuffer(GL30.GL_DRAW_BUFFER, fboTo);
        for (int att : attachmentsToCopy) {
            GL43.glReadBuffer(att);
            GL43.glDrawBuffers(att);
            GL43.glBlitFramebuffer(0, 0, dimension.x, dimension.y, 0, 0, dimension.x, dimension.y, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);
        }
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

    public void bindTexture(int i) {
        this.getTexturePrograms().get(i).bindTexture(GL30.GL_TEXTURE_2D);
    }

    public void unBindTexture() {
        this.getTexturePrograms().get(0).unBindTexture();
    }

    public void clearFBO() {
        for (ITextureProgram textureProgram : this.getTexturePrograms()) {
            textureProgram.cleanUp();
        }
        this.getTexturePrograms().clear();
        GL30.glDeleteRenderbuffers(this.renderBufferId);
        GL30.glDeleteFramebuffers(this.frameBufferId);
    }
}
