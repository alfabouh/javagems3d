package javagems3d.system.resources.managing;

import api.system.JGemsAPI;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.initialization.*;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.assets.texturing.ISample;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.JGemsSystemResources;
import javagems3d.system.resources.managing.resources.SystemResources;

import java.util.function.Function;

public final class JGemsResourceManager extends ResourceManager {
    public static GlobalShadersInitializer globalShaderAssets = null;
    public static TextureAssetsInitializer globalTextureAssets = null;
    public static ModelAssetsInitializer globalModelAssets = null;
    public static RenderDataInitializer globalRenderDataAssets = null;
    public static SoundAssetsInitializer globalSoundAssets = null;

    public JGemsResourceManager() {
        super(new Factory(ResourceManager.GLOBAL), new Factory(ResourceManager.LOCAL));
        JGemsResourceManager.globalShaderAssets = new GlobalShadersInitializer();
    }

    public static void createShaders() {
        JGemsResourceManager.globalShaderAssets.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersInitializer<? extends ShaderManager> shadersLoader : JGemsAPI.APIAppData().getAppResources().getShadersInitializers()) {
            shadersLoader.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }

        RenderDataInitializer.setDefaultRenderTableValues();
    }

    public static void reloadShaders() {
        JGemsResourceManager.globalShaderAssets.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersInitializer<? extends ShaderManager> shadersLoader : JGemsAPI.APIAppData().getAppResources().getShadersInitializers()) {
            shadersLoader.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }
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

    private Function<ISample.IProperties, ISample.IProperties> getTexturePropertiesProcessing() {
        return (e) -> {
            if (e instanceof ImageTexture) {
                ImageTexture imageTexture = (ImageTexture) e;
                ImageTexture.Properties properties = (ImageTexture.Properties) imageTexture.getProperties();
                boolean linear = properties.isLinearFiltration() && JGems3D.get().getGameSettings().texturesFiltering.getValue() == 1;
                boolean anisotropic = properties.isAnisotropicFiltration() && JGems3D.get().getGameSettings().anisotropic.getValue() == 1;
                return new ImageTexture.Properties(properties.isMipMap(), linear, properties.isShouldBeRepeated(), anisotropic, properties.isQualityAffected());
            }
            return null;
        };
    }

    public void reloadTexturesInGlobalCache() {
        this.getGlobalResources().reloadSamplesInCache(this.getTexturePropertiesProcessing(), false);
    }

    public void reloadTexturesInLocalCache() {
        this.getLocalResources().reloadSamplesInCache(this.getTexturePropertiesProcessing(), false);
    }

    public void recreateTexturesInAllCaches() {
        this.reloadTexturesInGlobalCache();
        this.reloadTexturesInLocalCache();
    }

    public SystemResources getLocalResources() {
        return this.getGameResources(ResourceManager.LOCAL);
    }

    public SystemResources getGlobalResources() {
        return this.getGameResources(ResourceManager.GLOBAL);
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
