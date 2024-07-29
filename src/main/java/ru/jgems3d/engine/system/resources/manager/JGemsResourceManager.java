package ru.jgems3d.engine.system.resources.manager;

import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.CubeMapProgram;
import ru.jgems3d.engine.system.resources.assets.shaders.ShaderContainer;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.exceptions.JGemsException;
import ru.jgems3d.engine.system.files.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.*;
import ru.jgems3d.engine.system.resources.assets.loaders.ShadersAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import ru.jgems3d.engine.system.resources.assets.materials.samples.CubeMapTextureArray;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.toolbox.ToolBox;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JGemsResourceManager {
    public static Map<String, CubeMapProgram> skyBoxTexturesMap = new HashMap<>();

    public static ShadersAssetsLoader globalShaderAssets = null;
    public static TextureAssetsLoader globalTextureAssets = null;
    public static ModelAssetsLoader globalModelAssets = null;
    public static RenderDataLoader globalRenderDataAssets = null;
    public static SoundAssetsLoader globalSoundAssetsLoader = null;

    private final GameResources globalResources;
    private final GameResources localResources;

    public JGemsResourceManager() {
        JGemsResourceManager.globalShaderAssets = new ShadersAssetsLoader();
        this.globalResources = new GameResources(new ResourceCache("Global"));
        this.localResources = new GameResources(new ResourceCache("Local"));
    }

    public static CubeMapProgram createSkyBoxCubeMap(JGPath pathToSkyBox, String format) {
        CubeMapProgram cubeMap = new CubeMapProgram();
        cubeMap.generateCubeMapFromTexture(new CubeMapTextureArray(pathToSkyBox, format));
        JGemsResourceManager.skyBoxTexturesMap.put(pathToSkyBox.getSPath(), cubeMap);
        return cubeMap;
    }

    public static void createShaders() {
        JGemsResourceManager.globalShaderAssets.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersLoader shadersLoader : APIContainer.get().getAppResourceLoader().getShadersLoaders()) {
            shadersLoader.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }
    }

    public static void reloadShaders() {
        JGemsResourceManager.globalShaderAssets.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersLoader shadersLoader : APIContainer.get().getAppResourceLoader().getShadersLoaders()) {
            shadersLoader.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }
    }

    public void destroy() {
        GuiFont.allCreatedFonts.forEach(GuiFont::cleanUp);
        this.cleanAllCaches();
    }

    public static Font createFontFromJAR(JGPath path) {
        Font font1;
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, JGems.loadFileJar(path));
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
        JGemsResourceManager.globalTextureAssets = new TextureAssetsLoader();
        JGemsResourceManager.globalModelAssets = new ModelAssetsLoader();
        JGemsResourceManager.globalRenderDataAssets = new RenderDataLoader();
        JGemsResourceManager.globalSoundAssetsLoader = new SoundAssetsLoader();
        this.getGlobalResources().addAssetsLoaders(JGemsResourceManager.globalTextureAssets, JGemsResourceManager.globalModelAssets, JGemsResourceManager.globalRenderDataAssets, JGemsResourceManager.globalSoundAssetsLoader);
        this.getGlobalResources().addAssetsLoaders(APIContainer.get().getAppResourceLoader().getAssetsLoaderSet());
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
