package javagems3d.system.resources.assets.texturing.packs;

import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.system.resources.assets.initialization.TextureAssetsInitializer;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;

public final class ParticleTexturesPack {
    private final ITextureProgram[] iImageSample;
    private final int texturesNum;
    private final float animationRateSeconds;
    private final JGemsPath pathToTexturePath;
    private final String format;

    public ParticleTexturesPack(JGemsPath pathToTexturePath, String format, int texturesNum, float animationRateSeconds) {
        this.iImageSample = new ITextureProgram[texturesNum];
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

    public ITextureProgram[] getTextureSamples() {
        return this.iImageSample;
    }

    private void loadTextures() {
        Log.get().info("Loading particle texture pack: " + this.pathToTexturePath);
        for (int i = 0; i < this.texturesNum; i++) {
            this.iImageSample[i] = JGemsResourceManager.getGlobalGameResources().createTexture(ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(this.pathToTexturePath, String.format("%s%d%s", "particle_", i, this.format)), new ImageTexture.Properties(true, true));
        }
    }
}