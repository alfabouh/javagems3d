package workbench.resources;

import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import workbench.WBench;
import workbench.resources.initialization.*;

public final class WBenchResourceManager extends ResourceManager {
    public static GlobalShadersInitializer globalShaderAssets = null;
    public static LocalShadersInitializer localShaderAssets = null;

    public static TextureAssetsInitializer localTextureAssets = null;
    public static ModelAssetsInitializer localModelAssets = null;

    public static ObjectsAssetsInitializer objectsAssetsInitializer = null;

    public WBenchResourceManager() {
        super(new Factory(ResourceManager.GLOBAL), new Factory(ResourceManager.LOCAL));
        WBenchResourceManager.globalShaderAssets = new GlobalShadersInitializer();
        WBenchResourceManager.localShaderAssets = new LocalShadersInitializer();
    }

    public static void setDefaultRenderTableValues() {
        JGemsShaderManager DEFAULT_SCENE_SHADER = WBenchResourceManager.localShaderAssets.world_gbuffer;
        JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER = WBenchResourceManager.localShaderAssets.depth_sun;
        JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER = WBenchResourceManager.localShaderAssets.depth_plight;
        JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER = WBenchResourceManager.localShaderAssets.weighted_oit;
        JGemsShaderManager DEFAULT_SCENE_SHADER_IND = WBenchResourceManager.localShaderAssets.world_gbuffer_indirect;
        JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = WBenchResourceManager.localShaderAssets.depth_sun_indirect;
        JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = WBenchResourceManager.localShaderAssets.depth_plight;
        JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER_IND = WBenchResourceManager.localShaderAssets.weighted_oit_indirect;

        RenderTable.SET_DEFAULT_SHADERS_DIRECT(DEFAULT_SCENE_SHADER, DEFAULT_SUN_L_SHADOW_MAP_SHADER, DEFAULT_POINT_L_SHADOW_MAP_SHADER, DEFAULT_TRANSPARENCY_SHADER);
        RenderTable.SET_DEFAULT_SHADERS_INDIRECT(DEFAULT_SCENE_SHADER_IND, DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND, DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND, DEFAULT_TRANSPARENCY_SHADER_IND);
    }

    public static void createGlobalShaders() {
        WBenchResourceManager.globalShaderAssets.createShaders(WBenchResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static void reloadGlobalShaders() {
        WBenchResourceManager.globalShaderAssets.reloadShaders(WBenchResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static void createLocalShaders() {
        WBenchResourceManager.localShaderAssets.createShaders(WBenchResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static void reloadLocalShaders() {
        WBenchResourceManager.localShaderAssets.reloadShaders(WBenchResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static void reloadShaders() {
        WBenchResourceManager.reloadLocalShaders();
        WBenchResourceManager.reloadGlobalShaders();
    }

    public static SystemResources getGlobalGameResources() {
        return WBench.get().getResourceManager().getGlobalResources();
    }

    public static ITexture2DProgram getAnimationsTextureBuffer() {
        return WBench.get().getResourceManager().getAnimationMatricesTexture();
    }

    public void loadLocalResources() {
        this.getLocalResources().loadResources();
    }

    public void destroyLocalResources() {
        this.getLocalResources().destroy();
    }

    public void loadGlobalResources() {
        this.getGlobalResources().loadResources();
    }

    public void initGlobalResources() {
        this.getGlobalResources().addAssetsLoaders();
    }

    public void initLocalResources() {
        WBenchResourceManager.localTextureAssets = new TextureAssetsInitializer();
        WBenchResourceManager.localModelAssets = new ModelAssetsInitializer();
        WBenchResourceManager.objectsAssetsInitializer = new ObjectsAssetsInitializer();
        this.getLocalResources().addAssetsLoaders(WBenchResourceManager.localTextureAssets, WBenchResourceManager.localModelAssets, WBenchResourceManager.objectsAssetsInitializer);
    }

    public SystemResources getGlobalResources() {
        return this.getGameResources(ResourceManager.GLOBAL);
    }

    public SystemResources getLocalResources() {
        return this.getGameResources(ResourceManager.LOCAL);
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