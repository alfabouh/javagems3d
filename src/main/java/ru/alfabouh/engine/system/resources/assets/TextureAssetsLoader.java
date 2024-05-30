package ru.alfabouh.engine.system.resources.assets;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.materials.textures.ParticleTexturePack;
import ru.alfabouh.engine.system.resources.assets.materials.textures.TextureSample;
import ru.alfabouh.engine.system.resources.cache.GameCache;
import ru.alfabouh.engine.render.scene.gui.elements.base.font.FontCode;
import ru.alfabouh.engine.render.scene.gui.elements.base.font.GuiFont;
import ru.alfabouh.engine.render.scene.programs.CubeMapProgram;

import java.awt.*;

public class TextureAssetsLoader implements IAssetsLoader {
    public TextureSample enemyTexture;
    public TextureSample waterTexture;
    public TextureSample waterNormals;
    public ParticleTexturePack particleTexturePack;
    public CubeMapProgram skyboxCubeMap;
    public CubeMapProgram skyboxCubeMap2;
    public GuiFont standardFont2;
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


    public TextureSample soda_inventory;
    public TextureSample soda_world;

    public TextureSample cd_world;
    public TextureSample cassette_world;

    public TextureSample screen;
    public TextureSample blood;

    public TextureSample[] emp = new TextureSample[6];

    public void load(GameCache gameCache) {
        JGems.get().getScreen().addLineInLoadingScreen("Loading textures...");
        Font gameFont = ResourceManager.createFontFromJAR("gamefont.ttf");
        this.standardFont2 = new GuiFont(gameCache, gameFont.deriveFont(Font.PLAIN, 18), FontCode.Window);
        this.standardFont = new GuiFont(gameCache, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);
        this.buttonFont = new GuiFont(gameCache, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);

        this.zippo_inventory = ResourceManager.createTexture("/assets/textures/items/zippo/zippo_inventory.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.emp_inventory = ResourceManager.createTexture("/assets/textures/items/emp/emp_inventory.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.crowbar_inventory = ResourceManager.createTexture("/assets/textures/items/crowbar/crowbar_inventory.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.radio_inventory = ResourceManager.createTexture("/assets/textures/items/radio/radio_inventory.png", false, GL30.GL_CLAMP_TO_EDGE);

        this.screen = ResourceManager.createTexture("/assets/textures/gui/screen.png", true, GL30.GL_REPEAT);

        this.waterNormals = ResourceManager.createTexture("/assets/textures/liquids/water_n.png", true, GL30.GL_REPEAT);
        this.waterTexture = ResourceManager.createTexture("/assets/textures/liquids/water.png", true, GL30.GL_REPEAT);
        this.crosshair = ResourceManager.createTexture("/assets/textures/gui/crosshair.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.gui1 = ResourceManager.createTexture("/assets/textures/gui/gui1.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.blood = ResourceManager.createTexture("/assets/textures/gui/blood.png", false, GL30.GL_CLAMP_TO_BORDER);
        this.radio = ResourceManager.createTexture("/assets/textures/items/radio/radio.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.radio_world = ResourceManager.createTexture("/assets/textures/items/radio/radio_world.png", false, GL30.GL_REPEAT);
        this.zippo1 = ResourceManager.createTexture("/assets/textures/items/zippo/zippo1.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.zippo_world = ResourceManager.createTexture("/assets/textures/items/zippo/zippo_world.png", false, GL30.GL_REPEAT);
        this.crowbar = ResourceManager.createTexture("/assets/textures/items/crowbar/crowbar.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.crowbar_world = ResourceManager.createTexture("/assets/textures/items/crowbar/crowbar_world.png", false, GL30.GL_REPEAT);

        this.cd_world = ResourceManager.createTexture("/assets/textures/items/pick/cd.png", false, GL30.GL_REPEAT);
        this.cassette_world = ResourceManager.createTexture("/assets/textures/items/pick/cassette.png", false, GL30.GL_REPEAT);

        this.soda_world = ResourceManager.createTexture("/assets/textures/items/soda/soda_world.png", false, GL30.GL_REPEAT);
        this.soda_inventory = ResourceManager.createTexture("/assets/textures/items/soda/soda_inventory.png", false, GL30.GL_REPEAT);

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
        JGems.get().getScreen().addLineInLoadingScreen("Textures successfully loaded...");
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
