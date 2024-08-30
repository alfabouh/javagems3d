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

package javagems3d.engine.system.resources.assets.material.samples.packs;

import javagems3d.engine.JGemsHelper;
import javagems3d.engine.system.resources.assets.loaders.TextureAssetsLoader;
import javagems3d.engine.system.resources.assets.material.samples.TextureSample;
import javagems3d.engine.system.resources.assets.material.samples.base.ITextureSample;
import javagems3d.engine.system.resources.manager.JGemsResourceManager;
import javagems3d.engine.system.service.path.JGemsPath;

public class ParticleTexturePack {
    private final ITextureSample[] iImageSample;
    private final int texturesNum;
    private final float animationRateSeconds;
    private final JGemsPath pathToTexturePath;
    private final String format;

    public ParticleTexturePack(JGemsPath pathToTexturePath, String format, int texturesNum, float animationRateSeconds) {
        this.iImageSample = new ITextureSample[texturesNum];
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

    public ITextureSample[] getiImageSample() {
        return this.iImageSample;
    }

    private void loadTextures() {
        JGemsHelper.getLogger().log("Loading particle texture pack: " + this.pathToTexturePath);
        for (int i = 0; i < this.texturesNum; i++) {
            this.iImageSample[i] = JGemsResourceManager.getGlobalGameResources().createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(this.pathToTexturePath, String.format("%s%d%s", "particle_", i, this.format)), new TextureSample.Params(true));
        }
    }
}
