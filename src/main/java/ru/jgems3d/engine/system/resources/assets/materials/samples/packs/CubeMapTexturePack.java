package ru.jgems3d.engine.system.resources.assets.materials.samples.packs;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class CubeMapTexturePack {
    private final TextureSample[] textures;

    @SuppressWarnings("all")
    public CubeMapTexturePack(JGPath pathToSkyBox, String format) {
        this.textures = new TextureSample[6];
        for (int i = 0; i < 6; i++) {
            StringBuilder builder = new StringBuilder();
            builder.append("sky_");
            builder.append(i + 1);
            builder.append(format);
            this.textures[i] = JGemsResourceManager.getGlobalGameResources().createTexture(new JGPath(pathToSkyBox, builder.toString()), true, GL30.GL_REPEAT);
        }
    }

    public TextureSample[] getTextureArray() {
        return this.textures;
    }
}