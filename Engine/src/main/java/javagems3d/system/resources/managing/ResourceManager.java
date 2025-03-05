package javagems3d.system.resources.managing;

import javagems3d.help.JGemsRenderingHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.Texture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.texturing.Color4Texture;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.resources.managing.resources.data.ResourcesDataCache;
import javagems3d.system.resources.managing.resources.data.cache.BindlessTexturesDataCache;
import javagems3d.system.resources.managing.resources.data.cache.MeshBuffersDataCache;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ResourceManager {
    private static ITexture2DProgram DEFAULT_TEXTURE = null;

    public static final String GLOBAL = "Global";
    public static final String LOCAL = "Local";
    private final Map<String, SystemResources> gameResourcesMap;
    private final ResourcesDataCache resourcesDataCache;
    private ITexture2DProgram animationMatricesTexture;

    public ResourceManager(Factory... factories) {
        this.gameResourcesMap = new HashMap<>();
        for (Factory factory : factories) {
            this.gameResourcesMap.put(factory.getId(), factory.createObject(factory.getId()));
        }
        this.resourcesDataCache = new ResourcesDataCache(new MeshBuffersDataCache(), new BindlessTexturesDataCache());
    }

    public void loadBindlessHandlersInSSBO(ShaderStorageBufferObject shaderStorageBufferObject) {
        LongBuffer longBuffer = MemoryUtil.memAllocLong(JGemsConfig.SYSTEM.MAX_BINDLESS_TEXTURES);
        for (ITextureBindless l : this.getResourceDataCache().getBindlessTexturesCache().getBindlessTexturesIdMap().keySet()) {
            longBuffer.put(l.getBindingHandler());
        }
        longBuffer.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(shaderStorageBufferObject, 0L, longBuffer);
        MemoryUtil.memFree(longBuffer);
    }

    public void loadMeshMaterialsIsSSBO(ShaderStorageBufferObject shaderStorageBufferObject) {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(Float.BYTES * JGemsConfig.SYSTEM.INDIRECT_RENDERING_MATERIALS_PACK_SIZE * JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_MATERIALS);
        for (Material material : this.getResourceDataCache().getMeshBuffersDataCache().getMaterials()) {
            ISample diffuse = material.getDiffuse();
            ISample normals = material.getNormalsMap();
            ISample emission = material.getEmissionMap();
            ISample specular = material.getSpecularMap();
            ISample metallic = material.getMetallicMap();
            if (diffuse instanceof Color4Texture) {
                Color4Texture color4Texture = (Color4Texture) diffuse;
                byteBuffer.putFloat(color4Texture.getColor().x);
                byteBuffer.putFloat(color4Texture.getColor().y);
                byteBuffer.putFloat(color4Texture.getColor().z);
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
            byteBuffer.putInt(JGemsRenderingHelper.getTexturingCodeForShader(material));
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

    public void writeResourcesDataCache() {
        this.getResourceDataCache().writeAll(this.gameResourcesMap.values().stream().map(SystemResources::getResourceArrays).collect(Collectors.toList()));
    }

    public void destroyResourcesDataCache() {
        this.getResourceDataCache().clearAll();
    }

    public void destroy() {
        ResourceManager.destroyDefaultTexture();
        ShaderStorageBufferProgram.clearAll();
        this.getResourceDataCache().clearAll();
        this.clearAll();

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
        Log.get().debug("Loading " + totalMatrices + " animations in texture-buffer");
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

    public void clearAll() {
        for (SystemResources systemResources : this.gameResourcesMap.values()) {
            systemResources.clearCache();
        }
    }

    public SystemResources getGameResources(String id) {
        return this.gameResourcesMap.get(id);
    }

    public ITexture2DProgram getAnimationMatricesTexture() {
        return this.animationMatricesTexture;
    }

    public ResourcesDataCache getResourceDataCache() {
        return this.resourcesDataCache;
    }

    public static void destroyDefaultTexture() {
        if (ResourceManager.DEFAULT_TEXTURE != null) {
            ResourceManager.DEFAULT_TEXTURE.clear();
            ResourceManager.DEFAULT_TEXTURE = null;
        }
    }

    public static void initDefaultTexture() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * 4);
            buffer.put(new float[] {0.0f, 0.0f, 0.0f});
            buffer.put(new float[] {1.0f, 0.0f, 1.0f});
            buffer.put(new float[] {0.0f, 0.0f, 0.0f});
            buffer.put(new float[] {1.0f, 0.0f, 1.0f});
            ResourceManager.DEFAULT_TEXTURE = new Texture2DProgram();
            Texture2DProgram texture2DProgram = (Texture2DProgram) ResourceManager.DEFAULT_TEXTURE;
            texture2DProgram.createTexture(new Vector2i(2), new Texture2DProgram.Properties(GL46.GL_RGB, GL46.GL_RGB, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, null), buffer);
        }
    }

    public static @NotNull ITexture2DProgram DEFAULT_TEXTURE() {
        return ResourceManager.DEFAULT_TEXTURE;
    }

    public interface Factory {
        SystemResources createObject(String id);
        String getId();
    }
}
