package workbench.resources;

import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import workbench.WBench;
import workbench.resources.initialization.*;

public final class WBenchResourceManager extends ResourceManager {
    public static GBasicShadersInitializer globalShaderAssets = null;

    public static TextureAssetsInitializer localShaderAssets = null;
    public static TextureAssetsInitializer localTextureAssets = null;
    public static ModelAssetsInitializer localModelAssets = null;
    public static RenderDataInitializer localRenderDataAssets = null;

    public WBenchResourceManager() {
        super(new Factory(ResourceManager.GLOBAL), new Factory(ResourceManager.LOCAL));
        WBenchResourceManager.globalShaderAssets = new GBasicShadersInitializer();
    }

    public static void createShaders() {
        WBenchResourceManager.globalShaderAssets.createShaders(WBenchResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static void reloadShaders() {
        WBenchResourceManager.globalShaderAssets.reloadShaders(WBenchResourceManager.getGlobalGameResources().getResourceCache());
    }

    public static SystemResources getGlobalGameResources() {
        return WBench.get().getResourceManager().getGlobalResources();
    }

    public static ITextureProgram getAnimationsTextureBuffer() {
        return WBench.get().getResourceManager().getAnimationMatricesTexture();
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
        WBenchResourceManager.localRenderDataAssets = new RenderDataInitializer();
        this.getLocalResources().addAssetsLoaders(WBenchResourceManager.localTextureAssets, WBenchResourceManager.localModelAssets, WBenchResourceManager.localRenderDataAssets);
    }

    public void clearGlobalCache() {
        this.getGlobalResources().destroy();
    }

    public void reloadTexturesInGlobalCache() {
        this.getGlobalResources().reloadTexturesInCache();
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