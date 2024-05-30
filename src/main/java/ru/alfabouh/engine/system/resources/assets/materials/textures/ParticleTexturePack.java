package ru.alfabouh.engine.system.resources.assets.materials.textures;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.ResourceManager;

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
        JGems.get().getLogManager().log("Loading particle texture pack: " + this.path);
        for (int i = 0; i < this.texturesNum; i++) {
            this.iImageSample[i] = ResourceManager.createTexture(this.path + i + this.format, true, GL30.GL_REPEAT);
        }
    }
}
