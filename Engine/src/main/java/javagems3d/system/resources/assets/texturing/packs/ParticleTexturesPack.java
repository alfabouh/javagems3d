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

package javagems3d.system.resources.assets.texturing.packs;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.initialization.TextureAssetsInitializer;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.base.IImageBasedTexture;
import javagems3d.system.resources.manager.JGemsResourceManager;
import javagems3d.system.service.path.JGemsPath;

public final class ParticleTexturesPack {
    private final IImageBasedTexture[] iImageSample;
    private final int texturesNum;
    private final float animationRateSeconds;
    private final JGemsPath pathToTexturePath;
    private final String format;

    public ParticleTexturesPack(JGemsPath pathToTexturePath, String format, int texturesNum, float animationRateSeconds) {
        this.iImageSample = new IImageBasedTexture[texturesNum];
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

    public IImageBasedTexture[] getTextureSamples() {
        return this.iImageSample;
    }

    private void loadTextures() {
        JGemsHelper.getLogger().log("Loading particle texture pack: " + this.pathToTexturePath);
        for (int i = 0; i < this.texturesNum; i++) {
            this.iImageSample[i] = JGemsResourceManager.getGlobalGameResources().createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(this.pathToTexturePath, String.format("%s%d%s", "particle_", i, this.format)), new ImageTexture.Properties(true));
        }
    }
}