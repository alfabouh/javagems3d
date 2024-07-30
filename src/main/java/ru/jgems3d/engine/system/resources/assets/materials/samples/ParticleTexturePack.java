package ru.jgems3d.engine.system.resources.assets.materials.samples;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.TextureAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class ParticleTexturePack {
    private final IImageSample[] iImageSample;
    private final int texturesNum;
    private final float animationRateSeconds;
    private final JGPath pathToTexturePath;
    private final String format;

    public ParticleTexturePack(JGPath pathToTexturePath, String format, int texturesNum, float animationRateSeconds) {
        this.iImageSample = new IImageSample[texturesNum];
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

    public IImageSample[] getiImageSample() {
        return this.iImageSample;
    }

    private void loadTextures() {
        JGemsHelper.getLogger().log("Loading particle texture pack: " + this.pathToTexturePath);
        for (int i = 0; i < this.texturesNum; i++) {
            this.iImageSample[i] = JGemsResourceManager.getGlobalGameResources().createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(this.pathToTexturePath, String.format("%s%d%s", "particle_", i, this.format)), true, GL30.GL_REPEAT);
        }
    }
}
