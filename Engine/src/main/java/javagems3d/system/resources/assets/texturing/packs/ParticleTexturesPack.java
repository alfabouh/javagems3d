package javagems3d.system.resources.assets.texturing.packs;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public final class ParticleTexturesPack {
    private final ITexture2DProgram[] iImageSample;
    private final int texturesNum;
    private final float animationRateSeconds;
    private final JGemsPathSource pathToTexturePath;
    private final String format;

    public ParticleTexturesPack(@NotNull JGemsPathSource pathToTexturePath, String format, int texturesNum, float animationRateSeconds) {
        this.iImageSample = new ITexture2DProgram[texturesNum];
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

    public ITexture2DProgram[] getTextureSamples() {
        return this.iImageSample;
    }

    private void loadTextures() {
        Log.get().info("Loading particle texture pack: " + this.pathToTexturePath);
        for (int i = 0; i < this.texturesNum; i++) {
            //this.iImageSample[i] = JGemsHelper.resources().getGlobalGameResources().createTexture(this.source, ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(this.pathToTexturePath, String.format("%s%d%s", "particle_", i, this.format)), new ImageTexture.Properties(true, true));
        }
    }
}