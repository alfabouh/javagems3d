package ru.BouH.engine.game.resources.assets;

import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.textures.ParticleTexturePack;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.render.scene.objects.gui.font.FontCode;
import ru.BouH.engine.render.scene.objects.gui.font.FontTexture;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;

import java.awt.*;

public class TextureAssetsLoader implements IAssetsLoader {
    public TextureSample bricksNormals;
    public TextureSample grassNormals;
    public TextureSample grassSpecular;
    public TextureSample bricksTexture;
    public TextureSample grassTexture;
    public TextureSample guiTestImage;
    public TextureSample tallGrass;
    public TextureSample waterTexture;
    public ParticleTexturePack particleTexturePack;
    public CubeMapProgram skyboxCubeMap;
    public FontTexture standardFont;
    public TextureSample crosshair;

    public void load(GameCache gameCache) {
        this.standardFont = new FontTexture(new Font("Cambria", Font.PLAIN, 18), FontCode.Window);
        gameCache.addObjectInBuffer("font1", this.standardFont.getTexture());

        this.guiTestImage = ResourceManager.createTexture("/textures/gui/pictures/meme2.png");
        this.bricksTexture = ResourceManager.createTexture("/textures/props/bricks.png");
        this.bricksNormals = ResourceManager.createTexture("/textures/normals/bricks.png");
        this.grassNormals = ResourceManager.createTexture("/textures/normals/grass01_n.png");
        this.grassTexture = ResourceManager.createTexture("/textures/terrain/grass02.png");
        this.grassSpecular = ResourceManager.createTexture("/textures/normals/specular_grass.png");
        this.tallGrass = ResourceManager.createTexture("/textures/props/tallgrass.png");
        this.waterTexture = ResourceManager.createTexture("/textures/liquids/water.jpg");
        this.crosshair = ResourceManager.createTexture("/textures/gui/crosshair.png");
        this.particleTexturePack = new ParticleTexturePack("/textures/particles/flame/flame", ".png", 4, 0.25f);

        this.skyboxCubeMap = new CubeMapProgram();
        this.skyboxCubeMap.generateCubeMapFromTexture(new CubeMapProgram.CubeMapTextureArray("skybox/sky1", ".png"));
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.PRE;
    }

    @Override
    public int loadOrder() {
        return 0;
    }
}
