package ru.BouH.engine.game.resources.assets;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.textures.ParticleTexturePack;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.render.scene.gui.font.FontCode;
import ru.BouH.engine.render.scene.gui.font.GuiFont;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;

import java.awt.*;

public class TextureAssetsLoader implements IAssetsLoader {
    public TextureSample bricksNormals;
    public TextureSample grassNormals;
    public TextureSample grassSpecular;
    public TextureSample bricksTexture;
    public TextureSample grassTexture;
    public TextureSample guiTestImage;
    public TextureSample enemyTexture;
    public TextureSample tallGrass;
    public TextureSample waterTexture;
    public ParticleTexturePack particleTexturePack;
    public CubeMapProgram skyboxCubeMap;
    public GuiFont standardFont;
    public GuiFont buttonFont;
    public TextureSample crosshair;
    public TextureSample zippo1;
    public TextureSample zippo1_emission;
    public TextureSample zippo2;

    public void load(GameCache gameCache) {
        Game.getGame().getScreen().addLineInLoadingScreen("Loading textures...");
        this.standardFont = new GuiFont(new Font("Cambria", Font.PLAIN, 18), FontCode.Window);
        this.buttonFont = new GuiFont(new Font("Cambria", Font.PLAIN, 18), FontCode.Window);
        gameCache.addObjectInBuffer("font1", this.standardFont.getTexture());

        this.guiTestImage = ResourceManager.createTexture("/textures/gui/pictures/meme2.png", true);
        this.bricksTexture = ResourceManager.createTexture("/textures/props/bricks.png", true);
        this.bricksNormals = ResourceManager.createTexture("/textures/normals/bricks.png", true);
        this.grassNormals = ResourceManager.createTexture("/textures/normals/grass01_n.png", true);
        this.grassTexture = ResourceManager.createTexture("/textures/terrain/grass02.png", true);
        this.grassSpecular = ResourceManager.createTexture("/textures/normals/specular_grass.png", true);
        this.tallGrass = ResourceManager.createTexture("/textures/props/tallgrass.png", true);
        this.waterTexture = ResourceManager.createTexture("/textures/liquids/water.jpg", true);
        this.crosshair = ResourceManager.createTexture("/textures/gui/crosshair.png", true);
        this.zippo1 = ResourceManager.createTexture("/textures/items/zippo1.png", true);
        this.zippo1_emission = ResourceManager.createTexture("/textures/items/zippo1_emission.png", true);
        this.zippo2 = ResourceManager.createTexture("/textures/items/zippo2.png", true);
        this.enemyTexture = ResourceManager.createTexture("/textures/misc/enemy.png", false);
        this.particleTexturePack = new ParticleTexturePack("/textures/particles/flame/flame", ".png", 4, 0.25f);

        this.skyboxCubeMap = new CubeMapProgram();
        this.skyboxCubeMap.generateCubeMapFromTexture(new CubeMapProgram.CubeMapTextureArray("skybox/sky1", ".png"));
        Game.getGame().getScreen().addLineInLoadingScreen("Textures successfully loaded...");
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
