package ru.BouH.engine.game.resources.assets.materials.textures;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;

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
        Game.getGame().getLogManager().log("Loading particle texture pack: " + this.path);
        for (int i = 0; i < this.texturesNum; i++) {
            this.iImageSample[i] = ResourceManager.createTexture(this.path + i + this.format);
        }
    }
}
