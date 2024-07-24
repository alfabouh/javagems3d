package ru.alfabouh.jgems3d.engine.system.resources.manager;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.*;
import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.ShadersAssetsLoader;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;

import java.awt.*;
import java.io.IOException;

public class JGemsResourceManager {
    public static ShadersAssetsLoader globalShaderAssets = null;
    public static TextureAssetsLoader textureAssets = null;
    public static ModelAssetsLoader modelAssets = null;
    public static RenderDataLoader renderDataAssets = null;
    public static SoundAssetsLoader soundAssetsLoader = null;
    private final GameResources globalResources;
    private final GameResources localResources;

    public JGemsResourceManager() {
        JGemsResourceManager.globalShaderAssets = new ShadersAssetsLoader();
        this.globalResources = new GameResources(new ResourceCache("Global"));
        this.localResources = new GameResources(new ResourceCache("Local"));
    }

    public static void createShaders() {
        JGemsResourceManager.globalShaderAssets.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static void reloadShaders() {
        JGemsResourceManager.globalShaderAssets.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
    }

    public void destroy() {
        GuiFont.allCreatedFonts.forEach(GuiFont::cleanUp);
        this.cleanAllCaches();
    }

    public static Font createFontFromJAR(String font) {
        Font font1;
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, JGems.loadFileJar("/assets/jgems/" + font));
        } catch (FontFormatException | IOException e) {
            throw new JGemsException(e);
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font1);
        return font1;
    }

    public static GameResources getLocalGameResources() {
        return JGems.get().getResourceManager().getLocalResources();
    }

    public static GameResources getGlobalGameResources() {
        return JGems.get().getResourceManager().getGlobalResources();
    }

    public void loadGlobalResources() {
        this.getGlobalResources().loadResources();
    }

    public void loadLocalResources() {
        this.getLocalResources().loadResources();
    }

    public void initGlobalResources() {
        JGemsResourceManager.textureAssets = new TextureAssetsLoader();
        JGemsResourceManager.modelAssets = new ModelAssetsLoader();
        JGemsResourceManager.renderDataAssets = new RenderDataLoader();
        JGemsResourceManager.soundAssetsLoader = new SoundAssetsLoader();
        this.getGlobalResources().addAssetsLoaders(JGemsResourceManager.textureAssets, JGemsResourceManager.modelAssets, JGemsResourceManager.renderDataAssets, JGemsResourceManager.soundAssetsLoader);
    }

    public void initLocalResources() {
    }

    public void destroyLocalResources() {
        this.getLocalResources().destroy();
    }

    public void cleanGlobalCache() {
        this.getGlobalResources().cleanCache();
    }

    public void cleanLocalCache() {
        this.getLocalResources().cleanCache();
    }

    public void cleanAllCaches() {
        this.cleanLocalCache();
        this.cleanGlobalCache();
    }

    public void recreateTexturesInGlobalCache() {
        this.getGlobalResources().recreateTexturesInCache();
    }

    public void recreateTexturesInLocalCache() {
        this.getLocalResources().recreateTexturesInCache();
    }

    public void recreateTexturesInAllCaches() {
        this.recreateTexturesInGlobalCache();
        this.recreateTexturesInLocalCache();
    }

    public GameResources getLocalResources() {
        return this.localResources;
    }

    public GameResources getGlobalResources() {
        return this.globalResources;
    }
}
