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

import api.system.JGemsAPI;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.graphics.rendering.programs.textures.Texture2DProgram;
import javagems3d.graphics.rendering.programs.textures.ext.ITextureBindless;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.initialization.*;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.data.ResourcesDataCache;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.resources.managing.resources.data.cache.BindlessTexturesDataCache;
import javagems3d.system.resources.managing.resources.data.cache.MeshBuffersDataCache;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.util.Collection;

public final class JGemsResourceManager extends ResourceManager {
    public static final String GLOBAL = "Global";
    public static final String LOCAL = "Local";

    public static BasicShadersInitializer globalShaderAssets = null;
    public static TextureAssetsInitializer globalTextureAssets = null;
    public static ModelAssetsInitializer globalModelAssets = null;
    public static RenderDataInitializer globalRenderDataAssets = null;
    public static SoundAssetsInitializer globalSoundAssets = null;

    public JGemsResourceManager() {
        super(JGemsResourceManager.GLOBAL, JGemsResourceManager.LOCAL);
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

    public static GameResources getLocalGameResources() {
        return JGems3D.get().getResourceManager().getLocalResources();
    }

    public static GameResources getGlobalGameResources() {
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

    public GameResources getLocalResources() {
        return this.getGameResources(JGemsResourceManager.LOCAL);
    }

    public GameResources getGlobalResources() {
        return this.getGameResources(JGemsResourceManager.GLOBAL);
    }
}
