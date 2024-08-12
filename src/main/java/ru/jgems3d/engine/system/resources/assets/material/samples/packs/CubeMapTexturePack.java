package ru.jgems3d.engine.system.resources.assets.material.samples.packs;

import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.material.samples.TextureSample;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class CubeMapTexturePack {
    private final TextureSample[] textures;

    @SuppressWarnings("all")
    public CubeMapTexturePack(JGemsPath pathToSkyBox, String format) {
        this.textures = new TextureSample[6];
        for (int i = 0; i < 6; i++) {
            StringBuilder builder = new StringBuilder();
            builder.append("sky_");
            builder.append(i + 1);
            builder.append(format);
            this.textures[i] = JGemsResourceManager.getGlobalGameResources().createTexture(new JGemsPath(pathToSkyBox, builder.toString()), new TextureSample.Params());
        }
    }

    public TextureSample[] getTextureArray() {
        return this.textures;
    }
}
