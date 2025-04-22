package javagems3d.system.resources.assets.loading.models;

import javagems3d.JGems3D;
import javagems3d.help.JGemsUtils;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.loading.models.utils.AnimationLoadingUtils;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.components.Bone;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.loading.models.utils.ModelLoadingUtils;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.IntegerVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ModelMeshLoader implements ILoadingHelper {
    private final JGemsPath path;
    private final SystemResources systemResources;
    private JGemsShaderManager shaderToDetermineTexturesWithTransparency;
    public int countVertexes;

    public ModelMeshLoader(@NotNull SystemResources systemResources, @NotNull JGemsPath modelPath) {
        this.path = modelPath;
        this.systemResources = systemResources;
        this.shaderToDetermineTexturesWithTransparency = null;
        this.countVertexes = 0;
    }

    public MeshGroup createMeshGroup(@Nullable MeshCollisionData.Fabric fabric, boolean attachMeshBuffer, boolean keepNodesInMemory, boolean animated) {
        MeshGroup meshGroup = null;
        String grString = this.getStr(MeshGroup.POSTFIX);
        if (this.getResourceCache().checkObjectInCache(grString)) {
            meshGroup = this.getResourceCache().getCachedObjectUnSafeCast(grString);
            Log.get().info("Mesh " + this.getPath() + " picked from cache");
        } else {
            meshGroup = this.processMeshGroup(this.getShaderToDetermineTexturesWithTransparency(), this.getGameResources(), attachMeshBuffer, keepNodesInMemory, animated);
            this.getResourceCache().addObjectInBuffer(grString, meshGroup);
        }
        if (meshGroup == null) {
            throw new JGemsNullException("There was an error, while processing the model");
        }
        JGemsUtils.createMeshAABBData(meshGroup);
        if (DynamicsSystem.VALID) {
            JGemsUtils.createMeshCollisionData(meshGroup, fabric);
        }
        if (!keepNodesInMemory) {
            meshGroup.clearNodesData();
        }
        return meshGroup;
    }

    public MeshBuffer createMeshBuffer(@Nullable MeshCollisionData.Fabric fabric, boolean keepNodesInMemory, boolean animated) {
        String bffString = this.getStr(MeshBuffer.POSTFIX);
        MeshBuffer meshBuffer = null;
        if (this.isCacheValid() && this.getResourceCache().checkObjectInCache(bffString)) {
            meshBuffer = this.getResourceCache().getCachedObjectUnSafeCast(bffString);
            Log.get().info("Mesh " + this.getPath() + " picked from cache");
        } else {
            meshBuffer = this.processMeshBuffer(this.getShaderToDetermineTexturesWithTransparency(), this.getGameResources(), animated);
            this.getResourceCache().addObjectInBuffer(bffString, meshBuffer);
        }
        if (meshBuffer == null) {
            throw new JGemsNullException("There was an error, while processing the model");
        }
        JGemsUtils.createMeshAABBData(meshBuffer);
        if (DynamicsSystem.VALID) {
            JGemsUtils.createMeshCollisionData(meshBuffer, fabric);
        }
        meshBuffer.setKeepNodesInMemory(keepNodesInMemory);
        return meshBuffer;
    }

    public String getStr(String postfix) {
        return ModelMeshLoader.getModelStr(this.getPath(), postfix);
    }

    public static String getModelStr(JGemsPath path, String postfix) {
        return path + postfix;
    }

    //++++++++++++++++++++++++++

    private AIScene loadAIScene(MemoryStack stack, JGemsPath path, boolean hasAnim) {
        if (JGems3D.checkFileExistsInJar(path)) {
            int FLAGS = Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_ImproveCacheLocality | Assimp.aiProcess_OptimizeGraph | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_GenNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace;
            if (!hasAnim) {
                FLAGS |= Assimp.aiProcess_PreTransformVertices;
            }
            AIScene scene = Assimp.aiImportFileEx(path.getFullPath(), FLAGS, AIFileIO.calloc(stack).OpenProc(ModelLoadingUtils.AI_FILE_OPEN).CloseProc(ModelLoadingUtils.AI_FILE_CLOSE));
            if (scene != null) {
                return scene;
            } else {
                throw new JGemsNullException("Couldn't create AIScene for: " + path);
            }
        } else {
            throw new JGemsIOException("Couldn't find " + path);
        }
    }

    @SuppressWarnings("all")
    private SkeletonData readSkeleton(List<Bone> bonesList, AIScene scene, AIMesh aiMesh) {
        SkeletonData skeletonData = AnimationLoadingUtils.readSkeleton(aiMesh, bonesList);
        return skeletonData;
    }

    @SuppressWarnings("all")
    private void readAnimations(List<Bone> bonesList, AIScene scene, MeshStructure3D<?>... meshStructures) {
        List<Animation> animations = new ArrayList<>();
        int totalAnimations = scene.mNumAnimations();
        if (totalAnimations > 0) {
            Animation.Node rootNode = AnimationLoadingUtils.createNodesTree(scene.mRootNode(), null);
            Matrix4f globalInverseTransformation = AnimationLoadingUtils.toJOMLMatrix(scene.mRootNode().mTransformation()).invert();
            animations = AnimationLoadingUtils.readAnimation(scene, bonesList, rootNode, globalInverseTransformation);
            if (rootNode == null) {
                throw new JGemsIOException("Failed to create nodes in animated model");
            }
        }
        List<Animation> finalAnimations = animations;
        Arrays.stream(meshStructures).filter(Objects::nonNull).forEach(e -> e.loadAnimations(finalAnimations));
        Log.get().info("Loaded animation (size:" + animations.size() + ") for: " + this.getPath());
        systemResources.processMessage("Loaded animation(size:" + animations.size() + ")", 0xff00ff);
    }

    private MeshGroup processMeshGroup(@Nullable JGemsShaderManager computeTransparentPixels, SystemResources systemResources, boolean attachMeshBuffer, boolean keepMeshBufferNodesInMemory, boolean animated) {
        MeshGroup meshGroup = new MeshGroup();
        MeshBuffer meshBuffer = attachMeshBuffer ? new MeshBuffer() : null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene aiScene = this.loadAIScene(stack, this.getPath(), animated);
            int totalMaterials = aiScene.mNumMaterials();

            List<Material> materialList = new ArrayList<>();
            List<Bone> bonesList = new ArrayList<>();
            for (int i = 0; i < totalMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(aiScene.mMaterials()).get(i));
                Material material = ModelLoadingUtils.readMaterial(computeTransparentPixels, systemResources, aiMaterial, this.getPath().getDirectory().getFullPath());
                if (meshBuffer != null) {
                    systemResources.getResourceArrays().getMeshBuffersDataArray().addMaterial(material);
                }
                materialList.add(material);
            }

            systemResources.processMessage("Building Mesh Group...", 0x00ff00);

            int totalMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            SkeletonData skeletonData = null;
            if (totalMeshes == 0) {
                throw new JGemsIOException("Caught invalid model: " + this.getPath());
            }
            for (int i = 0; i < totalMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(Objects.requireNonNull(aiMeshes).get(i));
                skeletonData = this.readSkeleton(bonesList, aiScene, aiMesh);

                int matIdx = aiMesh.mMaterialIndex();
                Material material = matIdx >= 0 && matIdx < materialList.size() ? materialList.get(matIdx) : new Material();

                RenderMesh meshData = this.createRenderMesh(aiMesh, skeletonData);
                meshGroup.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(meshData, material));

                if (meshBuffer != null) {
                    DataMesh meshData2 = this.createDataMesh(aiMesh, skeletonData);
                    meshBuffer.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(meshData2, material));
                    systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(meshBuffer);
                    meshGroup.setLinkedMeshBuffer(meshBuffer);
                    meshBuffer.setKeepNodesInMemory(keepMeshBufferNodesInMemory);
                }
            }

            if (skeletonData != null) {
                this.readAnimations(bonesList, aiScene, meshGroup, meshBuffer);
            }

            Assimp.aiReleaseImport(aiScene);
            Log.get().info("Mesh " + this.getPath() + " successfully created");
        } catch (Exception e) {
            Log.get().error(e.getMessage());
            return null;
        }

        return meshGroup;
    }

    private MeshBuffer processMeshBuffer(@Nullable JGemsShaderManager computeTransparentPixels, SystemResources systemResources, boolean animated) {
        MeshBuffer meshBuffer = new MeshBuffer();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene aiScene = this.loadAIScene(stack, this.getPath(), animated);
            int totalMaterials = aiScene.mNumMaterials();

            List<Material> materialList = new ArrayList<>();
            List<Bone> bonesList = new ArrayList<>();
            for (int i = 0; i < totalMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(aiScene.mMaterials()).get(i));
                Material material = ModelLoadingUtils.readMaterial(computeTransparentPixels, systemResources, aiMaterial, this.getPath().getDirectory().getFullPath());
                systemResources.getResourceArrays().getMeshBuffersDataArray().addMaterial(material);
                materialList.add(material);
            }

            systemResources.processMessage("Building Mesh Buffer...", 0x00ff00);

            int totalMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            SkeletonData skeletonData = null;
            if (totalMeshes == 0) {
                throw new JGemsIOException("Caught invalid model: " + this.getPath());
            }
            for (int i = 0; i < totalMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(Objects.requireNonNull(aiMeshes).get(i));
                skeletonData = this.readSkeleton(bonesList, aiScene, aiMesh);

                int matIdx = aiMesh.mMaterialIndex();
                Material material = matIdx >= 0 && matIdx < materialList.size() ? materialList.get(matIdx) : new Material();

                DataMesh meshData = this.createDataMesh(aiMesh, skeletonData);
                meshBuffer.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(meshData, material));
                systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(meshBuffer);
            }
            if (skeletonData != null) {
                this.readAnimations(bonesList, aiScene, meshBuffer);
            }

            Assimp.aiReleaseImport(aiScene);
            Log.get().info("Mesh " + this.getPath() + " successfully created");
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }

        return meshBuffer;
    }

    private DataMesh createDataMesh(AIMesh aiMesh, SkeletonData skeletonData) {
        List<Integer> vertices = ModelLoadingUtils.readVertices(aiMesh);
        this.countVertexes += vertices.size();
        if (this.countVertexes > JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL) {
            throw new JGemsRuntimeException("Reached max vertexes in model: " + JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL);
        }
        List<Float> textureCoordinates = ModelLoadingUtils.readTextureCoordinates(aiMesh);
        List<Float> positions = ModelLoadingUtils.readPositions(aiMesh);
        List<Float> normals = ModelLoadingUtils.readNormals(aiMesh);
        List<Float> tangents = ModelLoadingUtils.readTangents(aiMesh);
        List<Float> biTangents = ModelLoadingUtils.readBiTangents(aiMesh);

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

    private RenderMesh createRenderMesh(AIMesh aiMesh, SkeletonData skeletonData) {
        List<Integer> vertices = ModelLoadingUtils.readVertices(aiMesh);
        this.countVertexes += vertices.size();
        if (this.countVertexes > JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL) {
            throw new JGemsRuntimeException("Reached max vertexes in model: " + JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL);
        }
        List<Float> textureCoordinates = ModelLoadingUtils.readTextureCoordinates(aiMesh);
        List<Float> positions = ModelLoadingUtils.readPositions(aiMesh);
        List<Float> normals = ModelLoadingUtils.readNormals(aiMesh);
        List<Float> tangents = ModelLoadingUtils.readTangents(aiMesh);
        List<Float> biTangents = ModelLoadingUtils.readBiTangents(aiMesh);

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

    public JGemsShaderManager getShaderToDetermineTexturesWithTransparency() {
        return this.shaderToDetermineTexturesWithTransparency;
    }

    public void setShaderToDetermineTexturesWithTransparency(JGemsShaderManager shaderToDetermineTexturesWithTransparency) {
        this.shaderToDetermineTexturesWithTransparency = shaderToDetermineTexturesWithTransparency;
    }

    public SystemResources getGameResources() {
        return this.systemResources;
    }

    public JGemsPath getPath() {
        return this.path;
    }

    public ResourceCache getResourceCache() {
        return this.getGameResources().getResourceCache();
    }
}