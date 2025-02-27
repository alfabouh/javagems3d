package javagems3d.system.resources.assets.texturing.packs;

import javagems3d.system.resources.assets.initialization.TextureAssetsInitializer;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;

public final class ParticleTexturesPack {
    private final ImageBasedTexture[] iImageSample;
    private final int texturesNum;
    private final float animationRateSeconds;
    private final JGemsPath pathToTexturePath;
    private final String format;

    public ParticleTexturesPack(JGemsPath pathToTexturePath, String format, int texturesNum, float animationRateSeconds) {
        this.iImageSample = new ImageBasedTexture[texturesNum];
        this.pathToTexturePath = pathToTexturePath;
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

    public ImageBasedTexture[] getTextureSamples() {
        return this.iImageSample;
    }

    private void loadTextures() {
        Log.get().info("Loading particle texture pack: " + this.pathToTexturePath);
        for (int i = 0; i < this.texturesNum; i++) {
            this.iImageSample[i] = JGemsResourceManager.getGlobalGameResources().createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(this.pathToTexturePath, String.format("%s%d%s", "particle_", i, this.format)), new ImageTexture.Properties(true, true));
        }
    }
}