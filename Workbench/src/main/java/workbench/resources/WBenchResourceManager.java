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
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.resources.managing.resources.data.ResourcesDataCache;
import javagems3d.system.resources.managing.resources.data.cache.BindlessTexturesDataCache;
import javagems3d.system.resources.managing.resources.data.cache.MeshBuffersDataCache;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import workbench.WBench;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.util.Collection;

public final class WBenchResourceManager extends ResourceManager {
    public static BasicShadersInitializer globalShaderAssets = null;
    public static TextureAssetsInitializer globalTextureAssets = null;
    public static ModelAssetsInitializer globalModelAssets = null;
    public static RenderDataInitializer globalRenderDataAssets = null;
    public static SoundAssetsInitializer globalSoundAssets = null;

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