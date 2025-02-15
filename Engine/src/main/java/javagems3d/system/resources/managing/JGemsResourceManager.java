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
import javagems3d.JGemsHelper;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.global.JGemsRenderingGlobalConstants;
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
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
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

public final class JGemsResourceManager {
    public static BasicShadersInitializer globalShaderAssets = null;
    public static TextureAssetsInitializer globalTextureAssets = null;
    public static ModelAssetsInitializer globalModelAssets = null;
    public static RenderDataInitializer globalRenderDataAssets = null;
    public static SoundAssetsInitializer globalSoundAssets = null;

    private final GameResources globalResources;
    private final GameResources localResources;

    private final ResourcesDataCache resourcesDataCache;

    private ITextureProgram animationMatricesTexture;

    public JGemsResourceManager() {
        JGemsResourceManager.globalShaderAssets = new BasicShadersInitializer();
        this.globalResources = new GameResources(new ResourceCache("Global"));
        this.localResources = new GameResources(new ResourceCache("Local"));

        this.resourcesDataCache = new ResourcesDataCache(new MeshBuffersDataCache(), new BindlessTexturesDataCache());
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

    public void loadBindlessHandlersInSSBO(ShaderStorageBufferObject shaderStorageBufferObject) {
        LongBuffer longBuffer = MemoryUtil.memAllocLong(JGemsGlobalConfiguration.MAX_BINDLESS_TEXTURES);
        for (ITextureBindless l : this.getResourceDataCache().getBindlessTexturesCache().getBindlessTexturesIdMap().keySet()) {
            longBuffer.put(l.getBindingHandler());
        }
        longBuffer.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(shaderStorageBufferObject, 0L, longBuffer);
        MemoryUtil.memFree(longBuffer);
    }

    public void loadMeshMaterialsIsSSBO(ShaderStorageBufferObject shaderStorageBufferObject) {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(Float.BYTES * JGemsGlobalConfiguration.INDIRECT_RENDERING_MATERIALS_PACK_SIZE * JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_MATERIALS);
        for (Material material : this.getResourceDataCache().getMeshBuffersDataCache().getMaterials()) {
            ISample diffuse = material.getDiffuse();
            ISample normals = material.getNormalsMap();
            ISample emission = material.getEmissionMap();
            ISample specular = material.getSpecularMap();
            ISample metallic = material.getMetallicMap();
            if (diffuse instanceof RGBAColor) {
                RGBAColor rgbaColor = (RGBAColor) diffuse;
                byteBuffer.putFloat(rgbaColor.getColor().x);
                byteBuffer.putFloat(rgbaColor.getColor().y);
                byteBuffer.putFloat(rgbaColor.getColor().z);
                byteBuffer.putFloat(material.getFullOpacity());
            } else {
                byteBuffer.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
            }
            BindlessTexturesDataCache bindlessTexturesDataCache = this.getResourceDataCache().getBindlessTexturesCache();
            byteBuffer.putInt(diffuse instanceof ITextureBindless ? bindlessTexturesDataCache.getTextureId((ITextureBindless) diffuse) : 0);
            byteBuffer.putInt(normals instanceof ITextureBindless ? bindlessTexturesDataCache.getTextureId((ITextureBindless) normals) : 0);
            byteBuffer.putInt(emission instanceof ITextureBindless ? bindlessTexturesDataCache.getTextureId((ITextureBindless) emission) : 0);
            byteBuffer.putInt(specular instanceof ITextureBindless ? bindlessTexturesDataCache.getTextureId((ITextureBindless) specular) : 0);
            byteBuffer.putInt(metallic instanceof ITextureBindless ? bindlessTexturesDataCache.getTextureId((ITextureBindless) metallic) : 0);
            byteBuffer.putInt(JGemsHelper.RENDERING.getTexturingCodeForShader(material));
            byteBuffer.putInt(0);
            byteBuffer.putInt(0);
        }
        byteBuffer.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(shaderStorageBufferObject, 0L, byteBuffer);
        MemoryUtil.memFree(byteBuffer);
    }

    public void loadModelAnimationsInTexture() {
        this.animationMatricesTexture = this.createAnimationsTexture(this.getResourceDataCache().getMeshBuffersDataCache().getMeshBuffers());
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

    public void writeResourcesDataCache() {
        this.getResourceDataCache().writeAll(this.getGlobalResources().getResourceArrays(), this.getLocalResources().getResourceArrays());
    }

    public void destroyResourcesDataCache() {
        this.getResourceDataCache().clearAll();
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

    public void destroy() {
        ShaderStorageBufferProgram.clearAll();
        GuiFont.allCreatedFonts.forEach(GuiFont::clear);
        this.getResourceDataCache().clearAll();
        this.clearAllCaches();

        if (this.getAnimationMatricesTexture() != null) {
            this.getAnimationMatricesTexture().clear();
            this.animationMatricesTexture = null;
        }
    }

    private Texture2DProgram createAnimationsTexture(Collection<? extends MeshStructure3D<?>> meshStructuresCollection) {
        if (this.getAnimationMatricesTexture() != null) {
            this.getAnimationMatricesTexture().clear();
        }
        int totalMatrices = 0;
        for (MeshStructure3D<?> meshStructure3D : meshStructuresCollection) {
            for (Animation animation : meshStructure3D.getAnimationsList()) {
                animation.setOffset(totalMatrices);
                for (AnimationFrame animationFrame : animation.getFrameList()) {
                    animationFrame.setOffset(totalMatrices);
                    totalMatrices += animationFrame.getBoneMatrices().length;
                }
            }
        }
        JGemsHelper.getLogger().debug("Loading " + totalMatrices + " animations in texture-buffer");
        Texture2DProgram texture2DProgram = new Texture2DProgram();
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(totalMatrices * 16);
        for (MeshStructure3D<?> meshStructure3D : meshStructuresCollection) {
            for (Animation animation : meshStructure3D.getAnimationsList()) {
                for (AnimationFrame animationFrame : animation.getFrameList()) {
                    for (Matrix4f matrix4f : animationFrame.getBoneMatrices()) {
                        floatBuffer.put(matrix4f.get(new float[16]));
                    }
                }
            }
        }
        floatBuffer.flip();
        int s = (int) Math.ceil(Math.sqrt(totalMatrices * 4));
        texture2DProgram.createTexture(new Vector2i(s), new Texture2DProgram.Properties(GL46.GL_RGBA32F, GL46.GL_RGBA, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, null), floatBuffer);
        MemoryUtil.memFree(floatBuffer);
        return texture2DProgram;
    }

    public ITextureProgram getAnimationMatricesTexture() {
        return this.animationMatricesTexture;
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

    public ResourcesDataCache getResourceDataCache() {
        return this.resourcesDataCache;
    }

    public GameResources getLocalResources() {
        return this.localResources;
    }

    public GameResources getGlobalResources() {
        return this.globalResources;
    }
}
