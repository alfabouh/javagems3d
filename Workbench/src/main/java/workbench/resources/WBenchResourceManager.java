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

package workbench.resources;

import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.GameResources;
import workbench.WBench;
import workbench.resources.initialization.*;

public final class WBenchResourceManager extends ResourceManager {
    public static BasicShadersInitializer globalShaderAssets = null;
    public static TextureAssetsInitializer globalTextureAssets = null;
    public static ModelAssetsInitializer globalModelAssets = null;
    public static RenderDataInitializer globalRenderDataAssets = null;

    public WBenchResourceManager() {
        super(JGemsResourceManager.GLOBAL);
        WBenchResourceManager.globalShaderAssets = new BasicShadersInitializer();
    }

    public static void createShaders() {
        WBenchResourceManager.globalShaderAssets.createShaders(WBenchResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static void reloadShaders() {
        WBenchResourceManager.globalShaderAssets.reloadShaders(WBenchResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static GameResources getGlobalGameResources() {
        return WBench.get().getResourceManager().getGlobalResources();
    }

    public static ITextureProgram getAnimationsTextureBuffer() {
        return WBench.get().getResourceManager().getAnimationMatricesTexture();
    }

    public void loadGlobalResources() {
        this.getGlobalResources().loadResources();
    }

    public void initGlobalResources() {
        WBenchResourceManager.globalTextureAssets = new TextureAssetsInitializer();
        WBenchResourceManager.globalModelAssets = new ModelAssetsInitializer();
        WBenchResourceManager.globalRenderDataAssets = new RenderDataInitializer();
        this.getGlobalResources().addAssetsLoaders(WBenchResourceManager.globalTextureAssets, WBenchResourceManager.globalModelAssets, WBenchResourceManager.globalRenderDataAssets);
    }

    public void clearGlobalCache() {
        this.getGlobalResources().destroy();
    }

    public void reloadTexturesInGlobalCache() {
        this.getGlobalResources().reloadTexturesInCache();
    }

    public GameResources getGlobalResources() {
        return this.getGameResources(JGemsResourceManager.GLOBAL);
    }
}