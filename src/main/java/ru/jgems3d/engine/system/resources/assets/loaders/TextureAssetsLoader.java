package ru.jgems3d.engine.system.resources.assets.loaders;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.FontCode;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.jgems3d.engine.system.resources.assets.material.samples.CubeMapSample;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.resources.assets.material.samples.packs.ParticleTexturePack;
import ru.jgems3d.engine.system.resources.assets.material.samples.TextureSample;
import ru.jgems3d.engine.system.resources.manager.GameResources;

import java.awt.*;

public class TextureAssetsLoader implements IAssetsLoader {
    public static TextureSample DEFAULT;
    public static JGemsPath defaultSkyCubeMapPath = new JGemsPath(JGems3D.Paths.CUBE_MAPS, "default", "sky_");

    public TextureSample waterTexture;
    public TextureSample waterNormals;
    public ParticleTexturePack particleTexturePack;
    public CubeMapSample defaultSkyboxCubeMap;
    public CubeMapSample skyboxCubeMap;
    public CubeMapSample skyboxCubeMap2;
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

    public void load(GameResources gameResources) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading textures...");
        TextureAssetsLoader.DEFAULT = gameResources.createTexture(new JGemsPath(JGems3D.Paths.TEXTURES, "default.png"), new TextureSample.Params(false, true, false, false));

        Font gameFont = JGemsResourceManager.createFontFromJAR(new JGemsPath("/assets/jgems/gamefont.ttf"));

        this.standardFont2 = new GuiFont(gameResources.getResourceCache(), gameFont.deriveFont(Font.PLAIN, 18), FontCode.Window);
        this.standardFont = new GuiFont(gameResources.getResourceCache(), gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);
        this.buttonFont = new GuiFont(gameResources.getResourceCache(), gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);

        this.zippo_inventory = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo_inventory.png"), new TextureSample.Params(false, false, false, false));

        this.waterNormals = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "liquids/water_n.png"), new TextureSample.Params(true));
        this.waterTexture = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "liquids/water.png"), new TextureSample.Params(true));
        this.crosshair = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "gui/crosshair.png"), new TextureSample.Params(false, false, false, false));
        this.gui1 = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "gui/gui1.png"), new TextureSample.Params(false, false, false, false));
        this.zippo1 = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo1.png"), new TextureSample.Params(false, false, false, false));
        this.zippo_world = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo_world.png"), new TextureSample.Params(false, false, false, false));

        this.zippo1_emission = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo1_emission.png"), new TextureSample.Params(false, false, false, false));
        this.zippo2 = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo2.png"), new TextureSample.Params(false, false, false, false));
        this.particleTexturePack = new ParticleTexturePack(new JGemsPath(JGems3D.Paths.PARTICLES, "flame"), ".png", 4, 0.25f);

        this.defaultSkyboxCubeMap = gameResources.createCubeMap(TextureAssetsLoader.defaultSkyCubeMapPath, ".png");
        this.skyboxCubeMap = gameResources.createCubeMap(new JGemsPath(JGems3D.Paths.CUBE_MAPS, "skyDay", "sky_"), ".png");
        this.skyboxCubeMap2 = gameResources.createCubeMap(new JGemsPath(JGems3D.Paths.CUBE_MAPS, "skyNight", "sky_"), ".bmp");
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.NORMAL;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.HIGH;
    }
}
