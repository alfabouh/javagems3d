/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.managing;

import javagems3d.JGems3D;
import api.bridge.APIContainer;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.initialization.*;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.arrays.MeshBuffersDataArray;
import javagems3d.system.resources.managing.arrays.BindlessTexturesArray;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.resources.managing.resources.ResourceArrays;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public final class JGemsResourceManager {
    public static ShadersAssetsInitializer globalShaderAssets = null;
    public static TextureAssetsInitializer globalTextureAssets = null;
    public static ModelAssetsInitializer globalModelAssets = null;
    public static RenderDataInitializer globalRenderDataAssets = null;
    public static SoundAssetsInitializer globalSoundAssets = null;

    private final GameResources globalResources;
    private final GameResources localResources;

    private final ResourceArrays resourceArrays;

    public JGemsResourceManager() {
        JGemsResourceManager.globalShaderAssets = new ShadersAssetsInitializer();
        this.resourceArrays = new ResourceArrays(new MeshBuffersDataArray(), new BindlessTexturesArray());
        this.globalResources = new GameResources(new ResourceCache("Global"), this.getResourceArrays());
        this.localResources = new GameResources(new ResourceCache("Local"), this.getResourceArrays());
    }

    public static void createShaders() {
        JGemsResourceManager.globalShaderAssets.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersInitializer<JGemsShaderManager> shadersLoader : APIContainer.get().getAppResourceLoader().getShadersLoaders()) {
            shadersLoader.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }
    }

    public static void reloadShaders() {
        JGemsResourceManager.globalShaderAssets.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersInitializer<JGemsShaderManager> shadersLoader : APIContainer.get().getAppResourceLoader().getShadersLoaders()) {
            shadersLoader.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }
    }

    public static Font createFontFromJAR(JGemsPath path) {
        Font font1;
        try {
            try (InputStream inputStream = JGems3D.loadFileFromJar(path)) {
                font1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            }
        } catch (FontFormatException | IOException e) {
            throw new JGemsIOException(e);
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font1);
        return font1;
    }

    public static GameResources getLocalGameResources() {
        return JGems3D.get().getResourceManager().getLocalResources();
    }

    public static GameResources getGlobalGameResources() {
        return JGems3D.get().getResourceManager().getGlobalResources();
    }

    public void destroy() {
        ShaderStorageBufferProgram.clearAllSSBOs();
        GuiFont.allCreatedFonts.forEach(GuiFont::clear);
        this.getResourceArrays().clearAll();
        this.clearAllCaches();
    }

    public void loadGlobalResources() {
        this.getGlobalResources().loadResources();
    }

    public void loadLocalResources() {
        this.getLocalResources().loadResources();
    }

    public void initGlobalResources() {
        JGemsResourceManager.globalTextureAssets = new TextureAssetsInitializer();
        JGemsResourceManager.globalModelAssets = new ModelAssetsInitializer();
        JGemsResourceManager.globalRenderDataAssets = new RenderDataInitializer();
        JGemsResourceManager.globalSoundAssets = new SoundAssetsInitializer();
        this.getGlobalResources().addAssetsLoaders(JGemsResourceManager.globalTextureAssets, JGemsResourceManager.globalModelAssets, JGemsResourceManager.globalRenderDataAssets, JGemsResourceManager.globalSoundAssets);
        this.getGlobalResources().addAssetsLoaders(APIContainer.get().getAppResourceLoader().getAssetsLoaderSet());
    }

    public void initLocalResources() {
    }

    public void destroyLocalResources() {
        this.getLocalResources().destroy();
    }

    public void clearGlobalCache() {
        this.getGlobalResources().destroy();
    }

    public void clearLocalCache() {
        this.getLocalResources().destroy();
    }

    public void clearAllCaches() {
        this.clearLocalCache();
        this.clearGlobalCache();
    }

    public void reloadTexturesInGlobalCache() {
        this.getGlobalResources().reloadTexturesInCache();
    }

    public void reloadTexturesInLocalCache() {
        this.getLocalResources().reloadTexturesInCache();
    }

    public void recreateTexturesInAllCaches() {
        this.reloadTexturesInGlobalCache();
        this.reloadTexturesInLocalCache();
    }

    public ResourceArrays getResourceArrays() {
        return this.resourceArrays;
    }

    public GameResources getLocalResources() {
        return this.localResources;
    }

    public GameResources getGlobalResources() {
        return this.globalResources;
    }
}
