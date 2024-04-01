package ru.BouH.engine.game.resources.assets;

import org.lwjgl.opengl.GL30;
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
    public TextureSample enemyTexture;
    public TextureSample waterTexture;
    public ParticleTexturePack particleTexturePack;
    public CubeMapProgram skyboxCubeMap;
    public CubeMapProgram skyboxCubeMap2;
    public GuiFont standardFont;
    public GuiFont buttonFont;
    public TextureSample crosshair;
    public TextureSample gui1;
    public TextureSample zippo1;
    public TextureSample zippo1_emission;
    public TextureSample zippo2;
    public TextureSample crowbar;
    public TextureSample radio;

    public TextureSample zippo_world;
    public TextureSample emp_world;
    public TextureSample radio_world;
    public TextureSample crowbar_world;

    public TextureSample zippo_inventory;
    public TextureSample emp_inventory;
    public TextureSample crowbar_inventory;
    public TextureSample radio_inventory;

    public TextureSample[] emp = new TextureSample[6];

    public void load(GameCache gameCache) {
        Game.getGame().getScreen().addLineInLoadingScreen("Loading textures...");
        Font gameFont = ResourceManager.createFontFromJAR("gamefont.ttf");
        this.standardFont = new GuiFont(gameCache, gameFont.deriveFont(Font.BOLD, 18), FontCode.Window);
        this.buttonFont = new GuiFont(gameCache, gameFont.deriveFont(Font.BOLD, 18), FontCode.Window);

        this.zippo_inventory = ResourceManager.createTexture("/assets/textures/items/zippo/zippo_inventory.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.emp_inventory = ResourceManager.createTexture("/assets/textures/items/emp/emp_inventory.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.crowbar_inventory = ResourceManager.createTexture("/assets/textures/items/crowbar/crowbar_inventory.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.radio_inventory = ResourceManager.createTexture("/assets/textures/items/radio/radio_inventory.png", false, GL30.GL_CLAMP_TO_EDGE);

        this.waterTexture = ResourceManager.createTexture("/assets/textures/liquids/water.jpg", true, GL30.GL_REPEAT);
        this.crosshair = ResourceManager.createTexture("/assets/textures/gui/crosshair.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.gui1 = ResourceManager.createTexture("/assets/textures/gui/gui1.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.radio = ResourceManager.createTexture("/assets/textures/items/radio/radio.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.radio_world = ResourceManager.createTexture("/assets/textures/items/radio/radio_world.png", false, GL30.GL_REPEAT);
        this.zippo1 = ResourceManager.createTexture("/assets/textures/items/zippo/zippo1.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.zippo_world = ResourceManager.createTexture("/assets/textures/items/zippo/zippo_world.png", false, GL30.GL_REPEAT);
        this.crowbar = ResourceManager.createTexture("/assets/textures/items/crowbar/crowbar.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.crowbar_world = ResourceManager.createTexture("/assets/textures/items/crowbar/crowbar_world.png", false, GL30.GL_REPEAT);
        this.emp_world = ResourceManager.createTexture("/assets/textures/items/emp/emp_world.png", false, GL30.GL_REPEAT);
        this.zippo1_emission = ResourceManager.createTexture("/assets/textures/items/zippo/zippo1_emission.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.zippo2 = ResourceManager.createTexture("/assets/textures/items/zippo/zippo2.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.enemyTexture = ResourceManager.createTexture("/assets/textures/misc/enemy.png", false, GL30.GL_REPEAT);
        this.particleTexturePack = new ParticleTexturePack("/assets/textures/particles/flame/flame", ".png", 4, 0.25f);

        for (int i = 0; i < 6; i++) {
            this.emp[i] = ResourceManager.createTexture("/assets/textures/items/emp/emp" + i + ".png", false, GL30.GL_CLAMP_TO_EDGE);
        }

        this.skyboxCubeMap = new CubeMapProgram();
        this.skyboxCubeMap.generateCubeMapFromTexture(new CubeMapProgram.CubeMapTextureArray("skybox/sky1", ".png"));
        this.skyboxCubeMap2 = new CubeMapProgram();
        this.skyboxCubeMap2.generateCubeMapFromTexture(new CubeMapProgram.CubeMapTextureArray("skybox2/sky1", ".bmp"));
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
