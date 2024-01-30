package ru.BouH.engine.render.scene.programs;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.screen.Screen;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FrameBufferObjectProgram {
    private int frameBufferId;
    private int renderBufferId;
    private final List<TextureProgram> texturePrograms;
    private final boolean drawColor;
    private final boolean aliasing;
    private final int internalFormat;
    private final int textureFormat;
    private final int filtering1;
    private final int filtering2;
    private final int compareMode;
    private final int compareFunc;
    private final int clamp1;
    private final int clamp2;
    private final float[] borderColor;

    public FrameBufferObjectProgram(boolean drawColor, boolean aliasing, int internalFormat, int textureFormat, int filtering_mag, int filtering_min, int compareMode, int compareFunc, int clamp_s, int clamp_t, float[] borderColor) {
        this.texturePrograms = new ArrayList<>();
        this.drawColor = drawColor;
        this.aliasing = aliasing;
        this.internalFormat = internalFormat;
        this.textureFormat = textureFormat;
        this.filtering1 = filtering_mag;
        this.filtering2 = filtering_min;
        this.compareMode = compareMode;
        this.compareFunc = compareFunc;
        this.clamp1 = clamp_s;
        this.clamp2 = clamp_t;
        this.borderColor = borderColor;
    }

    public void createFrameBuffer(Vector2i size, int[] attachments, boolean depthBuffer) {
        if (!Scene.isSceneActive()) {
            return;
        }
        this.frameBufferId = GL30.glGenFramebuffers();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        for (final int attachment : attachments) {
            TextureProgram textureProgram = new TextureProgram();
            textureProgram.createTexture(size, this.internalFormat, this.textureFormat, this.filtering1, this.filtering2, this.compareMode, this.compareFunc, this.clamp1, this.clamp2, this.borderColor, this.aliasing);
            GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_TEXTURE_2D, textureProgram.getTextureId(), 0);
            this.getTexturePrograms().add(textureProgram);
        }
        if (!this.drawColor) {
            GL30.glDrawBuffer(GL30.GL_NONE);
            GL30.glReadBuffer(GL30.GL_NONE);
        } else {
            GL30.glDrawBuffers(attachments);
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
            Game.getGame().getLogManager().error("Failed to create framebuffer!");
        }

        this.unBindFBO();
    }

    public void connectTextureToBuffer(int attachment, int i) {
        GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_TEXTURE_2D, this.getTexturePrograms().get(i).getTextureId(), 0);
    }

    public int msaaSamples() {
        return Screen.MSAA_SAMPLES;
    }

    public List<TextureProgram> getTexturePrograms() {
        return this.texturePrograms;
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

    public void bindTexture(int i) {
        this.getTexturePrograms().get(i).bindTexture();
    }

    public void unBindTexture() {
        this.getTexturePrograms().get(0).unBindTexture();
    }

    public void clearFBO() {
        this.unBindFBO();
        this.unBindRenderDepthFBO();
        for (TextureProgram textureProgram : this.getTexturePrograms()) {
            textureProgram.cleanUp();
        }
        this.getTexturePrograms().clear();
        GL30.glDeleteRenderbuffers(this.renderBufferId);
        GL30.glDeleteFramebuffers(this.frameBufferId);
    }
}
