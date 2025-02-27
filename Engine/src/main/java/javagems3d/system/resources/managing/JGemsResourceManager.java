package javagems3d.system.resources.managing;

import api.system.JGemsAPI;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.system.resources.assets.initialization.*;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.JGemsSystemResources;
import javagems3d.system.resources.managing.resources.SystemResources;

public final class JGemsResourceManager extends ResourceManager {
    public static final String GLOBAL = "Global";
    public static final String LOCAL = "Local";

    public static BasicShadersInitializer globalShaderAssets = null;
    public static TextureAssetsInitializer globalTextureAssets = null;
    public static ModelAssetsInitializer globalModelAssets = null;
    public static RenderDataInitializer globalRenderDataAssets = null;
    public static SoundAssetsInitializer globalSoundAssets = null;

    public JGemsResourceManager() {
        super(new Factory(JGemsResourceManager.GLOBAL), new Factory(JGemsResourceManager.LOCAL));
        JGemsResourceManager.globalShaderAssets = new BasicShadersInitializer();
    }

    public static void createShaders() {
        JGemsResourceManager.globalShaderAssets.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersInitializer<? extends ShaderManager> shadersLoader : JGemsAPI.APIAppData().getAppResources().getShadersInitializers()) {
            shadersLoader.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }
    }

    public static void reloadShaders() {
        JGemsResourceManager.globalShaderAssets.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersInitializer<? extends ShaderManager> shadersLoader : JGemsAPI.APIAppData().getAppResources().getShadersInitializers()) {
            shadersLoader.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }
    }

    public static SystemResources getLocalGameResources() {
        return JGems3D.get().getResourceManager().getLocalResources();
    }

    public static SystemResources getGlobalGameResources() {
        return JGems3D.get().getResourceManager().getGlobalResources();
    }

    public static ITextureProgram getAnimationsTextureBuffer() {
        return JGems3D.get().getResourceManager().getAnimationMatricesTexture();
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
        this.getGlobalResources().addAssetsLoaders(JGemsAPI.APIAppData().getAppResources().getGlobalAssetsInitializers());
    }

    public void clearGlobalCache() {
        this.getGlobalResources().destroy();
    }

    public void clearLocalCache() {
        this.getLocalResources().destroy();
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

    public SystemResources getLocalResources() {
        return this.getGameResources(JGemsResourceManager.LOCAL);
    }

    public SystemResources getGlobalResources() {
        return this.getGameResources(JGemsResourceManager.GLOBAL);
    }

    private static class Factory implements ResourceManager.Factory {
        private final String id;

        public Factory(String id) {
            this.id = id;
        }

        @Override
        public SystemResources createObject(String id) {
            return new JGemsSystemResources(new ResourceCache(id));
        }

        @Override
        public String getId() {
            return this.id;
        }
    }
}
