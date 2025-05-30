package javagems3d.system.resources.assets.loading.models.gltf;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.help.JGemsUtils;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.GLTF2Parser;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.GLTF2RawData;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.*;
import javagems3d.system.resources.assets.loading.models.old.ModelMeshLoader;
import javagems3d.system.resources.assets.loading.models.old.utils.ModelLoadingUtils;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.animation.components.Bone;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.IntegerVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.texturing.colors.Color3Texture;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GLTF2ModelLoader implements ILoadingHelper {
    private final JGemsPath path;
    private final SystemResources systemResources;

    public GLTF2ModelLoader(JGemsPath pathToMainFile, SystemResources systemResources) {
        this.path = pathToMainFile;
        this.systemResources = systemResources;
    }

    public MeshGroup loadModel(@Nullable MeshCollisionData.Fabric fabric) {
        GLTF2RawData gltf2RawData = GLTF2Parser.parse(this.getPath());
        GLTF2Scene gltf2Scene = gltf2RawData.getGltf2Scene();
        return this.createMeshGroup(fabric, gltf2Scene);
    }

    public String getStr(String postfix) {
        return ModelMeshLoader.getModelStr(this.getPath(), postfix);
    }

    public MeshGroup createMeshGroup(@Nullable MeshCollisionData.Fabric fabric, GLTF2Scene gltf2Scene) {
        MeshGroup meshGroup = null;
        String grString = this.getStr("_gr");
        if (this.getResourceCache().checkObjectInCache(grString)) {
            meshGroup = this.getResourceCache().getCachedObjectUnSafeCast(grString);
            Log.get().info("Mesh " + this.getPath() + " picked from cache");
        } else {
            meshGroup = this.processMeshGroup(gltf2Scene, systemResources);
            this.getResourceCache().addObjectInBuffer(grString, meshGroup);
        }
        if (meshGroup == null) {
            throw new JGemsNullException("There was an error, while processing the model");
        }
        JGemsUtils.createMeshAABBData(meshGroup);
        if (DynamicsSystem.VALID) {
            JGemsUtils.createMeshCollisionData(meshGroup, fabric);
        }
        return meshGroup;
    }

    private MeshGroup processMeshGroup(GLTF2Scene gltf2Scene, SystemResources systemResources) {
        MeshGroup meshGroup = new MeshGroup();
        MeshBuffer meshBuffer = new MeshBuffer();

        try {
            int totalMaterials = gltf2Scene.getMaterials().size();
            List<Material> materialList = new ArrayList<>();
            for (int i = 0; i < totalMaterials; i++) {
                Material material = this.readMaterial(gltf2Scene.getMaterials().get(i), systemResources, this.getPath().getDirectory().getFullPath());
                systemResources.getResourceArrays().getMeshBuffersDataArray().addMaterial(material);
                materialList.add(material);
            }

            systemResources.processMessage("Building Mesh Group...", 0x00ff00);

            for (int i = 0; i < gltf2Scene.getNodes().size(); i++) {
                GLTF2Node node = gltf2Scene.getNodes().get(i);
                for (int j = 0; j < node.getMesh().getPrimitives().size(); j++) {
                    GLTF2Primitive primitive = node.getMesh().getPrimitives().get(j);
                    int matIdx = primitive.getMaterialId();
                    Material material = matIdx >= 0 && matIdx < materialList.size() ? materialList.get(matIdx) : new Material();

                    RenderMesh meshData = this.createRenderMesh(primitive, null);
                    meshGroup.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(meshData, material));

                    DataMesh meshData2 = this.createDataMesh(primitive, null);
                    meshBuffer.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(meshData2, material));
                    systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(meshBuffer);
                }

                meshGroup.setLinkedMeshBuffer(meshBuffer);
                meshBuffer.setKeepNodesInMemory(true);
            }

            Log.get().info("Mesh " + this.getPath() + " successfully created");
        } catch (Exception e) {
            Log.get().error(e.getMessage());
            return null;
        }

        return meshGroup;
    }

    private DataMesh createDataMesh(GLTF2Primitive gltf2Primitive, SkeletonData skeletonData) {
        List<Integer> vertices = gltf2Primitive.getIndices().getObjects();
        //this.countVertexes += vertices.size();
        //if (this.countVertexes > JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL) {
        //    throw new JGemsRuntimeException("Reached max vertexes in model: " + JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL);
        //}
        List<Float> textureCoordinates = gltf2Primitive.getTEXCOORD_0().getObjects();
        List<Float> positions = gltf2Primitive.getPOSITION().getObjects();
        List<Float> normals = gltf2Primitive.getNORMAL().getObjects();
        List<Float> tangents = gltf2Primitive.getTANGENT().getObjects();
        List<Float> biTangents = gltf2Primitive.getBiTANGENT().getObjects();

        if (textureCoordinates.isEmpty()) {
            int totalElements = (positions.size() / 3) * 2;
            textureCoordinates = new ArrayList<>(totalElements);
        }

        DataMesh dataMesh = new DataMesh();
        dataMesh.setSkeletonData(skeletonData);

        dataMesh.putVertexIndexes(vertices);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_POSITIONS, positions);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, textureCoordinates);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_NORMALS, normals);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_TANGENTS, tangents);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_BI_TANGENTS, biTangents);

        if (skeletonData != null) {
            dataMesh.putVertexBufferI(DefaultAttributePointers.ATTR_BONES_INDEXES, skeletonData.getBoneIds());
            dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_BONES_WEIGHTS, skeletonData.getWeights());
        }

        return dataMesh;
    }

    private RenderMesh createRenderMesh(GLTF2Primitive gltf2Primitive, SkeletonData skeletonData) {
        List<Integer> vertices = gltf2Primitive.getIndices().getObjects();
        //this.countVertexes += vertices.size();
        //if (this.countVertexes > JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL) {
        //    throw new JGemsRuntimeException("Reached max vertexes in model: " + JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL);
        //}
        List<Float> textureCoordinates = gltf2Primitive.getTEXCOORD_0().getObjects();
        List<Float> positions = gltf2Primitive.getPOSITION().getObjects();
        List<Float> normals = gltf2Primitive.getNORMAL().getObjects();
        List<Float> tangents = gltf2Primitive.getTANGENT().getObjects();
        List<Float> biTangents = gltf2Primitive.getBiTANGENT().getObjects();

        if (textureCoordinates.isEmpty()) {
            int totalElements = (positions.size() / 3) * 2;
            textureCoordinates = new ArrayList<>(totalElements);
        }

        RenderMesh renderMesh = new RenderMesh();
        renderMesh.setSkeletonData(skeletonData);

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);
        FloatVertexAttribute vaNormals = new FloatVertexAttribute(DefaultAttributePointers.ATTR_NORMALS);
        FloatVertexAttribute vaTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TANGENTS);
        FloatVertexAttribute vaBiTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BI_TANGENTS);

        renderMesh.setVertexIndexes(vertices);
        vaPositions.set(positions);
        vaNormals.set(normals);
        vaTextureCoordinates.set(textureCoordinates);
        vaTangents.set(tangents);
        vaBiTangents.set(biTangents);

        renderMesh.putVertexAttribute(vaPositions);
        renderMesh.putVertexAttribute(vaTextureCoordinates);
        renderMesh.putVertexAttribute(vaNormals);
        renderMesh.putVertexAttribute(vaTangents);
        renderMesh.putVertexAttribute(vaBiTangents);

        if (skeletonData != null) {
            IntegerVertexAttribute vaBonesIndexes = new IntegerVertexAttribute(DefaultAttributePointers.ATTR_BONES_INDEXES);
            FloatVertexAttribute vaBonesWeights = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BONES_WEIGHTS);
            vaBonesIndexes.put(skeletonData.getBoneIds());
            vaBonesWeights.put(skeletonData.getWeights());

            renderMesh.putVertexAttribute(vaBonesIndexes);
            renderMesh.putVertexAttribute(vaBonesWeights);
        }

        renderMesh.bakeMesh();
        return renderMesh;
    }

    private Material readMaterial(GLTF2Material gltf2Material, SystemResources systemResources, String fullPath) {
        float opacityConstant = 1.0f;
        float metallicFactor = 0.0f;
        float roughnessFactor = 0.5f;
        boolean textureIsImageAndHasAlphaPixels = false;

        Color4Texture diffuseColor = null;
        Color3Texture emissionColor = null;

        ITexture2DProgram diffuseMap = null;
        ITexture2DProgram emissionMap = null;
        ITexture2DProgram normalsMap = null;
        ITexture2DProgram metallicRoughnessMap = null;

        final ImageTexture.Properties imageProperties = new ImageTexture.Properties(true, true);

        if (gltf2Material.hasFlag(GLTF2Material.OPACITY)) {
            opacityConstant = gltf2Material.getOpacity();
        }

        Vector4f diffuseColorVec = gltf2Material.getDiffusionColor();
        if (diffuseColorVec != null) {
            diffuseColor = new Color4Texture(new Vector4f(diffuseColorVec.x, diffuseColorVec.y, diffuseColorVec.z, diffuseColorVec.w * opacityConstant));
        }

        Vector3f emissionColorVec = gltf2Material.getEmissionColor();
        if (emissionColorVec != null) {
            emissionColor = new Color3Texture(new Vector3f(emissionColorVec));
        }

        try {
            GLTF2ImageTexture diffuseTexture = gltf2Material.getDiffusionTexture();
            boolean nullColor = (diffuseColor == null);
            if (nullColor) {
                diffuseColor = new Color4Texture(new Vector4f(1.0f));
            }

            if (diffuseTexture != null) {
                diffuseMap = systemResources.createTexture(nullColor ? ResourceManager.DEFAULT_TEXTURE() : null, new JGemsPath(fullPath, diffuseTexture.getUri()), imageProperties);
               // if (computeTransparentPixels != null) {
               //     textureIsImageAndHasAlphaPixels = Material.Transparency.scanForAlphaPixels(computeTransparentPixels, diffuseMap);
               // }
            }

            GLTF2ImageTexture emissionTexture = gltf2Material.getEmissionTexture();
            if (emissionTexture != null) {
                emissionMap = systemResources.createTexture(null, new JGemsPath(fullPath, emissionTexture.getUri()), imageProperties);
                if (emissionColorVec == null) {
                    emissionColor = new Color3Texture(new Vector3f(1.0f));
                }
            }

            GLTF2ImageTexture metallicRoughnessTexture = gltf2Material.getMetallicRoughnessTexture();
            if (metallicRoughnessTexture != null) {
                metallicRoughnessMap = systemResources.createTexture(null, new JGemsPath(fullPath, metallicRoughnessTexture.getUri()), imageProperties);
                metallicFactor = gltf2Material.hasFlag(GLTF2Material.METALLIC_FACTOR) ? gltf2Material.getMetallicFactor() : 0.5f;
            } else {
                metallicFactor = gltf2Material.hasFlag(GLTF2Material.METALLIC_FACTOR) ? gltf2Material.getMetallicFactor() : 0.0f;
            }

            roughnessFactor = gltf2Material.hasFlag(GLTF2Material.ROUGHNESS_FACTOR) ? gltf2Material.getRoughnessFactor() : 0.5f;

            GLTF2ImageTexture normalsTexture = gltf2Material.getNormalTexture();
            if (normalsTexture != null) {
                normalsMap = systemResources.createTexture(null, new JGemsPath(fullPath, normalsTexture.getUri()), imageProperties);
            }
        } catch (JGemsException e) {
            Log.get().exception(e);
        }

        Material material = new Material.Builder()
                .diffuseMap(diffuseMap)
                .diffuseColor(diffuseColor)
                .emissionMap(emissionMap)
                .emissionColor(emissionColor)
                .metallicRoughnessMap(metallicRoughnessMap)
                .normalsMap(normalsMap)
                .metallicFactor(metallicFactor)
                .roughnessFactor(roughnessFactor)
                .build();

        material.getTransparency().setHasTransparentPixels(textureIsImageAndHasAlphaPixels);
        material.getTransparency().setOpacity(opacityConstant);

        return material;
    }

    public JGemsPath getPath() {
        return this.path;
    }

    public SystemResources getSystemResources() {
        return this.systemResources;
    }

    @Override
    public ResourceCache getResourceCache() {
        return this.getSystemResources().getResourceCache();
    }
}
