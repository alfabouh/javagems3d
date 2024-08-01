package ru.jgems3d.engine.system.resources.assets.loaders;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.FontCode;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.CubeMapProgram;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.resources.assets.materials.samples.ParticleTexturePack;
import ru.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.jgems3d.engine.system.resources.manager.GameResources;

import java.awt.*;

public class TextureAssetsLoader implements IAssetsLoader {
    public static TextureSample DEFAULT;

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

    public void load(GameResources gameResources) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen("Loading textures...");
        TextureAssetsLoader.DEFAULT = gameResources.createTexture(new JGPath(JGems3D.Paths.TEXTURES, "default.png"), false, GL30.GL_REPEAT);

        Font gameFont = JGemsResourceManager.createFontFromJAR(new JGPath("/assets/jgems/gamefont.ttf"));

        this.standardFont2 = new GuiFont(gameResources.getResourceCache(), gameFont.deriveFont(Font.PLAIN, 18), FontCode.Window);
        this.standardFont = new GuiFont(gameResources.getResourceCache(), gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);
        this.buttonFont = new GuiFont(gameResources.getResourceCache(), gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);

        this.zippo_inventory = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo_inventory.png"), false, GL30.GL_CLAMP_TO_EDGE);

        this.waterNormals = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(JGems3D.Paths.TEXTURES, "liquids/water_n.png"), true, GL30.GL_REPEAT);
        this.waterTexture = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(JGems3D.Paths.TEXTURES, "liquids/water.png"), true, GL30.GL_REPEAT);
        this.crosshair = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(JGems3D.Paths.TEXTURES, "gui/crosshair.png"), false, GL30.GL_CLAMP_TO_EDGE);
        this.gui1 = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(JGems3D.Paths.TEXTURES, "gui/gui1.png"), false, GL30.GL_CLAMP_TO_EDGE);
        this.zippo1 = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo1.png"), false, GL30.GL_CLAMP_TO_EDGE);
        this.zippo_world = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo_world.png"), false, GL30.GL_REPEAT);

        this.zippo1_emission = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo1_emission.png"), false, GL30.GL_CLAMP_TO_EDGE);
        this.zippo2 = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo2.png"), false, GL30.GL_CLAMP_TO_EDGE);
        this.particleTexturePack = new ParticleTexturePack(new JGPath(JGems3D.Paths.PARTICLES, "flame"), ".png", 4, 0.25f);

        this.defaultSkyboxCubeMap = JGemsResourceManager.createSkyBoxCubeMap(new JGPath(JGems3D.Paths.CUBE_MAPS, "default"), ".png");
        this.skyboxCubeMap = JGemsResourceManager.createSkyBoxCubeMap(new JGPath(JGems3D.Paths.CUBE_MAPS, "skyDay"), ".png");
        this.skyboxCubeMap2 = JGemsResourceManager.createSkyBoxCubeMap(new JGPath(JGems3D.Paths.CUBE_MAPS, "skyNight"), ".bmp");
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