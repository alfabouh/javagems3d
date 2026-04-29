package javagems3d.system.resources.managing;

import javagems3d.help.JGemsHelper;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.Texture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor3;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.resources.managing.resources.data.ResourcesDataCache;
import javagems3d.system.resources.managing.resources.data.bindless_rendering_cache.BindlessTexturesDataCache;
import javagems3d.system.resources.managing.resources.data.bindless_rendering_cache.MeshBuffersDataCache;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ResourceManager {
    private static ITexture2DProgram DEFAULT_TEXTURE = null;
    private static MeshGroup DEFAULT_CUBE_MESHGROUP = null;
    private static MeshBuffer DEFAULT_CUBE_MESHBUFFER = null;

    public static final String GLOBAL = "Global";
    public static final String LOCAL1 = "Local1";
    public static final String LOCAL2 = "Local2";
    public static final String LOCAL3 = "Local3";
    public static final String LOCAL4 = "Local4";

    private final Map<String, SystemResources> gameResourcesMap;
    private final ResourcesDataCache resourcesDataCache;
    private ITexture2DProgram animationMatricesTexture;

    public ResourceManager(Factory... factories) {
        this.gameResourcesMap = new HashMap<>();
        for (Factory factory : factories) {
            this.gameResourcesMap.put(factory.id(), factory.createObject(factory.id()));
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
            final BindlessTexturesDataCache bindlessTexturesDataCache = this.getResourceDataCache().getBindlessTexturesCache();

            ITexture2DProgram diffuseMap = material.getDiffuseMap();
            ISampleColor4 diffuseColor = material.getDiffuseColor();
            ITexture2DProgram emissionMap = material.getEmissionMap();
            ISampleColor3 emissionColor = material.getEmissionColor();
            ITexture2DProgram metallicRoughnessMap = material.getMetallicRoughnessMap();
            ITexture2DProgram normalsMap = material.getNormalsMap();
            float metallicFactor = material.getMetallicFactor();
            float roughnessFactor = material.getRoughnessFactor();

            //vec4 diffuse_color;
            byteBuffer.putFloat(diffuseColor.color().x);
            byteBuffer.putFloat(diffuseColor.color().y);
            byteBuffer.putFloat(diffuseColor.color().z);
            byteBuffer.putFloat(material.getTransparency().getOpacity());
            //vec3 emission_color;
            byteBuffer.putFloat(emissionColor == null ? 0.0f : emissionColor.color().x);
            byteBuffer.putFloat(emissionColor == null ? 0.0f : emissionColor.color().y);
            byteBuffer.putFloat(emissionColor == null ? 0.0f : emissionColor.color().z);
            byteBuffer.putFloat(0.0f); //PADDING
            //float metallic_factor;
            byteBuffer.putFloat(metallicFactor);
            //float roughness_factor;
            byteBuffer.putFloat(roughnessFactor);
            //int diffuse_map_id;
            byteBuffer.putInt((diffuseMap != null && diffuseMap.isBindless()) ? bindlessTexturesDataCache.getTextureId((ITextureBindless) diffuseMap) : 0);
            //int normals_map_id;
            byteBuffer.putInt((normalsMap != null && normalsMap.isBindless()) ? bindlessTexturesDataCache.getTextureId((ITextureBindless) normalsMap) : 0);
            //int emission_map_id;
            byteBuffer.putInt((emissionMap != null && emissionMap.isBindless()) ? bindlessTexturesDataCache.getTextureId((ITextureBindless) emissionMap) : 0);
            //int metallic_roughness_map_id;
            byteBuffer.putInt((metallicRoughnessMap != null && metallicRoughnessMap.isBindless()) ? bindlessTexturesDataCache.getTextureId((ITextureBindless) metallicRoughnessMap) : 0);
            //int texturing_code;
            byteBuffer.putInt(JGemsHelper.render().getTexturingCodeForShader(material));
            byteBuffer.putInt(0);
        }
        byteBuffer.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(shaderStorageBufferObject, 0L, byteBuffer);
        MemoryUtil.memFree(byteBuffer);
    }

    public void loadModelAnimationsInTexture() {
        this.animationMatricesTexture = this.createAnimationsTexture(new ArrayList<>() {{
            for (SystemResources systemResources : ResourceManager.this.gameResourcesMap.values()) {
                addAll(systemResources.getResourceArrays().getMeshesWithAnimation());
                systemResources.getResourceArrays().getMeshesWithAnimation().clear();
            }
        }});
    }

    public void writeResourcesDataCache() {
        this.getResourceDataCache().writeAll(this.gameResourcesMap.values().stream().map(SystemResources::getResourceArrays).toList());
    }

    public void destroyResourcesDataCache() {
        this.getResourceDataCache().clearAll();
    }

    public void destroy() {
        ResourceManager.destroyDefaults();
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
                for (AnimationFrame animationFrame : animation.frameList()) {
                    animationFrame.setOffset(totalMatrices);
                    totalMatrices += animationFrame.getBoneMatrices().length;
                }
            }
        }

        int totalPixels = totalMatrices * 4;
        int size = (int) Math.ceil(Math.sqrt(totalPixels));
        int totalTexels = size * size;
        int totalFloats = totalTexels * 4;

        Log.get().debug("Loading " + totalMatrices + " matrices in texture-buffer. Texture size: " + size);

        Texture2DProgram texture2DProgram = new Texture2DProgram(true);
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(totalFloats);

        int matricesWritten = 0;
        for (MeshStructure3D<?> meshStructure3D : meshStructuresCollection) {
            for (Animation animation : meshStructure3D.getAnimationsList()) {
                for (AnimationFrame animationFrame : animation.frameList()) {
                    for (Matrix4f matrix4f : animationFrame.getBoneMatrices()) {
                        if (matricesWritten < totalTexels / 4) {
                            floatBuffer.put(matrix4f.get(new float[16]));
                            matricesWritten++;
                        }
                    }
                }
            }
        }

        int remainingFloats = totalFloats - matricesWritten * 16;
        for (int i = 0; i < remainingFloats; i++) {
            floatBuffer.put(0.0f);
        }

        floatBuffer.flip();
        Texture2DProgram.Properties properties = new Texture2DProgram.Properties(GL46.GL_RGBA32F, GL46.GL_RGBA, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, null);
        if (totalMatrices == 0) {
            texture2DProgram.createTexture(new Vector2i(1), properties, floatBuffer);
        } else {
            texture2DProgram.createTexture(new Vector2i(size), properties, floatBuffer);
        }

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

    public static void destroyDefaults() {
        if (ResourceManager.DEFAULT_TEXTURE != null) {
            ResourceManager.DEFAULT_TEXTURE.clear();
            ResourceManager.DEFAULT_TEXTURE = null;
        }
        if (ResourceManager.DEFAULT_CUBE_MESHBUFFER != null) {
            ResourceManager.DEFAULT_CUBE_MESHBUFFER.clear();
            ResourceManager.DEFAULT_CUBE_MESHBUFFER = null;
        }

        if (ResourceManager.DEFAULT_CUBE_MESHGROUP != null) {
            ResourceManager.DEFAULT_CUBE_MESHGROUP.clear();
            ResourceManager.DEFAULT_CUBE_MESHGROUP = null;
        }
    }

    public static void initDefaults() {
        final int grid = 16;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat((grid * grid) * 3);
            for (int y = 0; y < grid; y++) {
                for (int x = 0; x < grid; x++) {
                    boolean isPink = ((x ^ y) & 1) == 0;
                    buffer.put((isPink ? 1.0f : 0.0f));
                    buffer.put(0.0f);
                    buffer.put((isPink ? 1.0f : 0.0f));
                }
            }
            buffer.flip();
            ResourceManager.DEFAULT_TEXTURE = new Texture2DProgram(true);
            Texture2DProgram texture2DProgram = (Texture2DProgram) ResourceManager.DEFAULT_TEXTURE;
            texture2DProgram.createTexture(new Vector2i(grid, grid), new Texture2DProgram.Properties(GL46.GL_RGB, GL46.GL_RGB, GL46.GL_NEAREST, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, GL46.GL_CLAMP_TO_EDGE, null), buffer);
        }
        ResourceManager.DEFAULT_CUBE_MESHBUFFER = IAssetsInitializer.createDefaultCube_MBuffer();
        ResourceManager.DEFAULT_CUBE_MESHGROUP = IAssetsInitializer.createDefaultCube_MGroup();
        ResourceManager.DEFAULT_CUBE_MESHGROUP.setLinkedMeshBuffer(ResourceManager.DEFAULT_CUBE_MESHBUFFER);
    }

    public static void CREATE_PHYS_FOR_DEFAULT_MODELS() {
        if (DynamicsSystem.VALID) {
            JGemsHelper.JGemsResources.createMeshCollisionData(ResourceManager.DEFAULT_CUBE_MESHBUFFER, null);
            JGemsHelper.JGemsResources.createMeshCollisionData(ResourceManager.DEFAULT_CUBE_MESHGROUP, null);
        }
    }

    public static @NotNull ITexture2DProgram DEFAULT_TEXTURE() {
        return ResourceManager.DEFAULT_TEXTURE;
    }

    public static @NotNull MeshGroup DEFAULT_CUBE_MESHGROUP() {
        return ResourceManager.DEFAULT_CUBE_MESHGROUP;
    }

    public static @NotNull MeshBuffer DEFAULT_CUBE_MESHBUFFER() {
        return ResourceManager.DEFAULT_CUBE_MESHBUFFER;
    }

    public interface Factory {
        SystemResources createObject(String id);
        String id();
    }
}
