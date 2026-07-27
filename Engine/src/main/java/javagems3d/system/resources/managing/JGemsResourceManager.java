/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.resources.managing;

import api.scripting.coding.env.internal.util.resources.init.JSDefaultGameResources;
import api.system.JGemsAPI;
import api.scripting.JavaToJsAPI;
import javagems3d.JGems3D;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.initialization.*;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.resources.assets.texturing.IPropertiesSample;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.assets.texturing.ISample;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.JGemsSystemResources;
import javagems3d.system.resources.managing.resources.SystemResources;

import java.util.function.Function;
import java.util.function.Supplier;

public final class JGemsResourceManager extends ResourceManager {
    public static GlobalShadersInitializer globalShaderAssets = null;
    public static TextureAssetsInitializer globalTextureAssets = null;
    public static ModelAssetsInitializer globalModelAssets = null;
    public static RenderDataInitializer globalRenderDataAssets = null;
    public static SoundAssetsInitializer globalSoundAssets = null;

    public JGemsResourceManager() {
        super(new Factory(ResourceManager.GLOBAL), new Factory(ResourceManager.LOCAL1));
        JGemsResourceManager.globalShaderAssets = new GlobalShadersInitializer();
    }

    public static void createShaders() {
        JGemsResourceManager.globalShaderAssets.createShaders(JGemsHelper.resources().getGlobalGameResources().getResourceCache());
        for (ShadersInitializer<? extends ShaderManager> shadersLoader : JGemsAPI.APIAppData().getAppResources().getShadersInitializers()) {
            shadersLoader.createShaders(JGemsHelper.resources().getGlobalGameResources().getResourceCache());
        }
        JavaToJsAPI.createJSShadersInitializer(JGemsHelper.resources().getGlobalGameResources()).createShaders(JGemsHelper.resources().getGlobalGameResources().getResourceCache());
        RenderDataInitializer.setDefaultRenderTableValues();
    }

    public static void reloadShaders() {
        JGemsResourceManager.globalShaderAssets.reloadShaders(JGemsHelper.resources().getGlobalGameResources().getResourceCache());
        for (ShadersInitializer<? extends ShaderManager> shadersLoader : JGemsAPI.APIAppData().getAppResources().getShadersInitializers()) {
            shadersLoader.reloadShaders(JGemsHelper.resources().getGlobalGameResources().getResourceCache());
        }
        JavaToJsAPI.createJSShadersInitializer(JGemsHelper.resources().getGlobalGameResources()).reloadShaders(JGemsHelper.resources().getGlobalGameResources().getResourceCache());
    }

    public void loadGlobalResources() {
        this.getGlobalResources().loadResources();
        {
            JSDefaultGameResources.init();
        }
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
        this.getGlobalResources().addAssetsLoaders(JavaToJsAPI.createJSAssetsInitializer(this));
    }

    public void clearGlobalCache() {
        this.getGlobalResources().destroy();
    }

    public void clearLocalCache() {
        this.getLocalResources().destroy();
    }

    public static Supplier<ImageTexture.Properties> getDefaultImageTexturePropertiesPreProcessor(ImageTexture.Properties raw) {
        boolean linear = raw.linearFiltration() && JGems3D.get().getGameSettings().texturesFiltering.getValue() == 1;
        boolean anisotropic = raw.anisotropicFiltration() && JGems3D.get().getGameSettings().anisotropic.getValue() == 1;
        return () -> new ImageTexture.Properties(raw.mipMap(), linear, raw.shouldBeRepeated(), anisotropic, raw.qualityAffected());
    }

    public static Function<IPropertiesSample, ISample.IProperties> getDefaultTexturePropertiesPreProcessor() {
        return (e) -> {
            if (e instanceof ImageTexture imageTexture) {
                ImageTexture.Properties properties = (ImageTexture.Properties) imageTexture.getProperties();
                boolean linear = properties.linearFiltration() && JGems3D.get().getGameSettings().texturesFiltering.getValue() == 1;
                boolean anisotropic = properties.anisotropicFiltration() && JGems3D.get().getGameSettings().anisotropic.getValue() == 1;
                return new ImageTexture.Properties(properties.mipMap(), linear, properties.shouldBeRepeated(), anisotropic, properties.qualityAffected());
            }
            return null;
        };
    }

    public void reloadTexturesInGlobalCache() {
        this.getGlobalResources().reloadSamplesInCache(JGemsResourceManager.getDefaultTexturePropertiesPreProcessor(), false);
    }

    public void reloadTexturesInLocalCache() {
        this.getLocalResources().reloadSamplesInCache(JGemsResourceManager.getDefaultTexturePropertiesPreProcessor(), false);
    }

    public void recreateTexturesInAllCaches() {
        this.reloadTexturesInGlobalCache();
        this.reloadTexturesInLocalCache();
    }

    public SystemResources getLocalResources() {
        return this.getGameResources(ResourceManager.LOCAL1);
    }

    public SystemResources getGlobalResources() {
        return this.getGameResources(ResourceManager.GLOBAL);
    }

    private record Factory(String id) implements ResourceManager.Factory {
        @Override
            public SystemResources createObject(String id) {
                return new JGemsSystemResources(new ResourceCache(id));
            }
        }
}
