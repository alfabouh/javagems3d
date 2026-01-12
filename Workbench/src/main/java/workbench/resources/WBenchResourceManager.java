package workbench.resources;

import javagems3d.JGems3D;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import workbench.WBench;
import workbench.resources.initialization.*;
import workbench.resources.initialization.game.WBenchGameEditorModelAssetsInitializer;
import workbench.resources.initialization.map.WBenchMapEditorObjectsAssetsInitializer;
import workbench.resources.initialization.game.WBenchGameEditorTextureAssetsInitializer;

public final class WBenchResourceManager extends ResourceManager {
    public static WBenchGlobalShadersInitializer globalShaderAssets = null;
    public static WBenchLocalShadersInitializer localShaderAssets = null;

    public static WBenchGameEditorTextureAssetsInitializer localTextureAssets = null;
    public static WBenchGameEditorModelAssetsInitializer localModelAssets = null;

    public static WBenchMapEditorObjectsAssetsInitializer objectsAssetsInitializer = null;

    public WBenchResourceManager() {
        super(new Factory(ResourceManager.GLOBAL), new Factory(ResourceManager.LOCAL1));
        WBenchResourceManager.globalShaderAssets = new WBenchGlobalShadersInitializer(JGems3D.GetSource.JAR);
        WBenchResourceManager.localShaderAssets = new WBenchLocalShadersInitializer();
    }

    public static void setDefaultRenderTableValues() {
        JGemsShaderManager DEFAULT_SCENE_SHADER = WBenchResourceManager.localShaderAssets.world_gbuffer;
        JGemsShaderManager DEFAULT_BACKGROUND_SHADER = WBenchResourceManager.localShaderAssets.background;
        JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER = WBenchResourceManager.localShaderAssets.depth_sun;
        JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER = WBenchResourceManager.localShaderAssets.depth_plight;
        JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER = WBenchResourceManager.localShaderAssets.weighted_oit;

        JGemsShaderManager DEFAULT_SCENE_SHADER_IND = WBenchResourceManager.localShaderAssets.world_gbuffer_indirect;
        JGemsShaderManager DEFAULT_BACKGROUND_SHADER_IND = WBenchResourceManager.localShaderAssets.background_indirect;
        JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = WBenchResourceManager.localShaderAssets.depth_sun_indirect;
        JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = WBenchResourceManager.localShaderAssets.depth_plight;
        JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER_IND = WBenchResourceManager.localShaderAssets.weighted_oit_indirect;

        RenderTable.SET_DEFAULT_SHADERS_DIRECT(DEFAULT_SCENE_SHADER, DEFAULT_BACKGROUND_SHADER, DEFAULT_SUN_L_SHADOW_MAP_SHADER, DEFAULT_POINT_L_SHADOW_MAP_SHADER, DEFAULT_TRANSPARENCY_SHADER);
        RenderTable.SET_DEFAULT_SHADERS_INDIRECT(DEFAULT_SCENE_SHADER_IND, DEFAULT_BACKGROUND_SHADER_IND, DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND, DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND, DEFAULT_TRANSPARENCY_SHADER_IND);
    }

    public static void createGlobalShaders() {
        WBenchResourceManager.globalShaderAssets.createShaders(WBenchResourceManager.GetGlobalResources().getResourceCache());
    }

    public static void reloadGlobalShaders() {
        WBenchResourceManager.globalShaderAssets.reloadShaders(WBenchResourceManager.GetGlobalResources().getResourceCache());
    }

    public static void createLocalGameEditorShaders() {
        WBenchResourceManager.localShaderAssets.createShaders(WBenchResourceManager.GetLocalGameResources().getResourceCache());
    }

    public static void reloadLocalGameEditorShaders() {
        WBenchResourceManager.localShaderAssets.reloadShaders(WBenchResourceManager.GetLocalGameResources().getResourceCache());
    }

    public static void reloadShaders() {
        WBenchResourceManager.reloadLocalGameEditorShaders();
        WBenchResourceManager.reloadGlobalShaders();
    }

    public static SystemResources GetLocalGameResources() {
        return WBench.get().getResourceManager().getGlobalResources();
    }

    public static SystemResources GetLocalMapEditorGameResources() {
        return WBench.get().getResourceManager().getLocalMapEditorResources();
    }

    public static SystemResources GetGlobalResources() {
        return WBench.get().getResourceManager().getGlobalResources();
    }

    public static ITexture2DProgram getAnimationsTextureBuffer() {
        return WBench.get().getResourceManager().getAnimationMatricesTexture();
    }

    public void loadLocalMapEditorResources() {
        this.getLocalMapEditorResources().loadResources();
    }

    public void destroyLocalMapEditorResources() {
        this.getLocalMapEditorResources().destroy();
    }

    public void loadLocalGameEditorResources() {
        this.getLocalGameEditorResources().loadResources();
    }

    public void destroyLocalGameEditorResources() {
        this.getLocalGameEditorResources().destroy();
    }

    public void loadGlobalResources() {
        this.getGlobalResources().loadResources();
    }

    public void initGlobalResources() {
        this.getGlobalResources().addAssetsLoaders();
    }

    public void initLocalGameEditorResources() {
        WBenchResourceManager.localTextureAssets = new WBenchGameEditorTextureAssetsInitializer();
        WBenchResourceManager.localModelAssets = new WBenchGameEditorModelAssetsInitializer();
        this.getLocalGameEditorResources().addAssetsLoaders(WBenchResourceManager.localTextureAssets, WBenchResourceManager.localModelAssets);
    }

    public void initLocalMapEditorResources() {
        WBenchResourceManager.objectsAssetsInitializer = new WBenchMapEditorObjectsAssetsInitializer();
        this.getLocalMapEditorResources().addAssetsLoaders(WBenchResourceManager.objectsAssetsInitializer);
    }

    public SystemResources getGlobalResources() {
        return this.getGameResources(ResourceManager.GLOBAL);
    }

    public SystemResources getLocalGameEditorResources() {
        return this.getGameResources(ResourceManager.LOCAL1);
    }

    public SystemResources getLocalMapEditorResources() {
        return this.getGameResources(ResourceManager.LOCAL2);
    }

    private static class Factory implements ResourceManager.Factory {
        private final String id;

        public Factory(String id) {
            this.id = id;
        }

        @Override
        public SystemResources createObject(String id) {
            return new WBenchResources(new ResourceCache(id));
        }

        @Override
        public String getId() {
            return this.id;
        }
    }
}