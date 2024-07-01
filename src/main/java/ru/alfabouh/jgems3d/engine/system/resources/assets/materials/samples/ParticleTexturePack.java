package ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class ParticleTexturePack {
    private final IImageSample[] iImageSample;
    private final int texturesNum;
    private final float animationRateSeconds;
    private final String path;
    private final String format;

    public ParticleTexturePack(String path, String format, int texturesNum, float animationRateSeconds) {
        this.iImageSample = new IImageSample[texturesNum];
        this.path = path;
        this.format = format;
        this.texturesNum = texturesNum;
        this.animationRateSeconds = animationRateSeconds;
        this.loadTextures();
    }

    public float getAnimationRate() {
        return this.animationRateSeconds;
    }

    public int getTexturesNum() {
        return this.texturesNum;
    }

    public IImageSample[] getiImageSample() {
        return this.iImageSample;
    }

    private void loadTextures() {
        SystemLogging.get().getLogManager().log("Loading particle texture pack: " + this.path);
        for (int i = 0; i < this.texturesNum; i++) {
            this.iImageSample[i] = ResourceManager.createTexture(this.path + i + this.format, true, GL30.GL_REPEAT);
        }
    }
}
