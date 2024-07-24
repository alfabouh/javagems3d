package ru.alfabouh.jgems3d.engine.system.resources.assets.loaders;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.FontCode;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.programs.textures.CubeMapProgram;
import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.ParticleTexturePack;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.alfabouh.jgems3d.engine.system.resources.manager.GameResources;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TextureAssetsLoader implements IAssetsLoader {
    public static Map<String, CubeMapProgram> skyBoxMap = new HashMap<>();
    public static TextureSample DEFAULT;

    public TextureSample enemyTexture;
    public TextureSample waterTexture;
    public TextureSample waterNormals;
    public ParticleTexturePack particleTexturePack;
    public CubeMapProgram defaultSkyboxCubeMap;
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
    public TextureSample zippo_world;
    public TextureSample zippo_inventory;


    public TextureSample screen;


    public void load(GameResources gameResources) {
        JGems.get().getScreen().tryAddLineInLoadingScreen("Loading textures...");
        TextureAssetsLoader.DEFAULT = gameResources.createTexture("/assets/jgems/textures/default.png", false, GL30.GL_REPEAT);

        Font gameFont = JGemsResourceManager.createFontFromJAR("gamefont.ttf");
        this.standardFont2 = new GuiFont(gameResources.getResourceCache(), gameFont.deriveFont(Font.PLAIN, 18), FontCode.Window);
        this.standardFont = new GuiFont(gameResources.getResourceCache(), gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);
        this.buttonFont = new GuiFont(gameResources.getResourceCache(), gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);

        this.zippo_inventory = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/items/zippo/zippo_inventory.png", false, GL30.GL_CLAMP_TO_EDGE);

        this.screen = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/gui/screen.png", true, GL30.GL_REPEAT);

        this.waterNormals = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/liquids/water_n.png", true, GL30.GL_REPEAT);
        this.waterTexture = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/liquids/water.png", true, GL30.GL_REPEAT);
        this.crosshair = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/gui/crosshair.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.gui1 = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/gui/gui1.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.zippo1 = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/items/zippo/zippo1.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.zippo_world = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/items/zippo/zippo_world.png", false, GL30.GL_REPEAT);

        this.zippo1_emission = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/items/zippo/zippo1_emission.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.zippo2 = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/items/zippo/zippo2.png", false, GL30.GL_CLAMP_TO_EDGE);
        this.enemyTexture = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, "/assets/jgems/textures/misc/enemy.png", false, GL30.GL_REPEAT);
        this.particleTexturePack = new ParticleTexturePack("/assets/jgems/textures/particles/flame/flame", ".png", 4, 0.25f);

        this.defaultSkyboxCubeMap = this.createSkyBoxCubeMap("default", ".png");

        this.skyboxCubeMap = this.createSkyBoxCubeMap("skyDay", ".png");
        this.skyboxCubeMap2 = this.createSkyBoxCubeMap("skyNight", ".bmp");

        JGems.get().getScreen().tryAddLineInLoadingScreen("Textures successfully loaded...");
    }

    private CubeMapProgram createSkyBoxCubeMap(String skyName, String format) {
        CubeMapProgram cubeMap = new CubeMapProgram();
        cubeMap.generateCubeMapFromTexture(new CubeMapProgram.CubeMapTextureArray(skyName + "/sky1", format));
        TextureAssetsLoader.skyBoxMap.put(skyName, cubeMap);
        return cubeMap;
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
