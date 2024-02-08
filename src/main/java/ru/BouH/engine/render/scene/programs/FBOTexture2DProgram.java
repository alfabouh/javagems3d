package ru.BouH.engine.render.scene.programs;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class FBOTexture2DProgram {
    private int frameBufferId;
    private int renderBufferId;
    private final List<TextureProgram> texturePrograms;
    private final boolean drawColor;
    private final boolean aliasing;

    public FBOTexture2DProgram(boolean drawColor, boolean aliasing) {
        this.texturePrograms = new ArrayList<>();
        this.drawColor = drawColor;
        this.aliasing = aliasing;
    }

    public void createFrameBuffer2DTexture(Vector2i size, int[] attachments, boolean depthBuffer, int internalFormat, int textureFormat, int filtering, int compareMode, int compareFunc, int clamp, float[] borderColor) {
        if (!Scene.isSceneActive()) {
            return;
        }
        this.frameBufferId = GL30.glGenFramebuffers();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        for (final int attachment : attachments) {
            TextureProgram textureProgram = new TextureProgram();
            textureProgram.createTexture(size, internalFormat, textureFormat, filtering, filtering, compareMode, compareFunc, clamp, clamp, borderColor, this.aliasing);
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
                GL43.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, this.msaaSamples(), GL30.GL_DEPTH_COMPONENT, size.x, size.y);
            } else {
                GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT, size.x, size.y);
            }
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, this.renderBufferId);
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
