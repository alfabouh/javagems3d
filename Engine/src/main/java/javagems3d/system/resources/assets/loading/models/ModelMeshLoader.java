package javagems3d.system.resources.assets.loading.models;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.loading.models.utils.AnimationLoadingUtils;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.components.Bone;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.loading.models.utils.ModelLoadingUtils;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.IntegerVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
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
    private final GameResources gameResources;

    public ModelMeshLoader(@NotNull GameResources gameResources, @NotNull JGemsPath modelPath) {
        this.path = modelPath;
        this.gameResources = gameResources;
    }

    public MeshGroup createMeshGroup(int Flags, boolean attachMeshBuffer, MemMode mode) {
        boolean animated = (Flags & ModelLoaderFlags.LOAD_ANIMATIONS) != 0;
        boolean createCollision = (Flags & ModelLoaderFlags.CREATE_COLLISION_UD) != 0;
        boolean createAabb = (Flags & ModelLoaderFlags.CREATE_AABB_UD) != 0;
        MeshGroup meshGroup = null;
        String grString = this.getStr(MeshGroup.POSTFIX);
        if (this.getResourceCache().checkObjectInCache(grString)) {
            meshGroup = this.getResourceCache().getCachedObjectUnSafeCast(grString);
            JGemsHelper.getLogger().info("Mesh " + this.getPath() + " picked from cache");
        } else {
            meshGroup = this.processMeshGroup(this.getGameResources(), animated, attachMeshBuffer);
            this.getResourceCache().addObjectInBuffer(grString, meshGroup);
            JGemsHelper.getLogger().info("Mesh " + this.getPath() + " successfully created");
        }
        if (meshGroup == null) {
            throw new JGemsNullException("There was an error, while loading the model");
        }
        if (createCollision) {
            JGemsHelper.UTILS.createMeshCollisionData(meshGroup);
        }
        if (createAabb) {
            JGemsHelper.UTILS.createMeshAABBData(meshGroup);
        }
        meshGroup.setMemMode(mode);
        if (meshGroup.getMemMode().equals(MemMode.ERASE_NODES_DATA)) {
            meshGroup.clearNodesData();
        }
        return meshGroup;
    }

    public MeshBuffer createMeshBuffer(int Flags, MemMode mode) {
        boolean animated = (Flags & ModelLoaderFlags.LOAD_ANIMATIONS) != 0;
        boolean createCollision = (Flags & ModelLoaderFlags.CREATE_COLLISION_UD) != 0;
        boolean createAabb = (Flags & ModelLoaderFlags.CREATE_AABB_UD) != 0;
        String bffString = this.getStr(MeshBuffer.POSTFIX);
        MeshBuffer meshBuffer = null;
        if (this.isCacheValid() && this.getResourceCache().checkObjectInCache(bffString)) {
            meshBuffer = this.getResourceCache().getCachedObjectUnSafeCast(bffString);
            JGemsHelper.getLogger().info("Mesh " + this.getPath() + " picked from cache");
        } else {
            meshBuffer = this.processMeshBuffer(this.getGameResources(), animated);
            this.getResourceCache().addObjectInBuffer(bffString, meshBuffer);
            JGemsHelper.getLogger().info("Mesh " + this.getPath() + " successfully created");
        }
        if (meshBuffer == null) {
            throw new JGemsNullException("There was an error, while loading the model");
        }
        if (createCollision) {
            JGemsHelper.UTILS.createMeshCollisionData(meshBuffer);
        }
        if (createAabb) {
            JGemsHelper.UTILS.createMeshAABBData(meshBuffer);
        }
        meshBuffer.setMemMode(mode);
        return meshBuffer;
    }

    public String getStr(String postfix) {
        return ModelMeshLoader.getModelStr(this.getPath(), postfix);
    }

    public static String getModelStr(JGemsPath path, String postfix) {
        return path + postfix;
    }

    //++++++++++++++++++++++++++

    private AIScene loadAIScene(MemoryStack stack, JGemsPath path, boolean isAnimated) {
        if (JGems3D.checkFileExistsInJar(path)) {
            int FLAGS = Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_ImproveCacheLocality | Assimp.aiProcess_OptimizeGraph | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_GenNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace;
            if (!isAnimated) {
                FLAGS |= (Assimp.aiProcess_PreTransformVertices);
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
    private SkeletonData readSkeleton(List<Bone> bonesList, AIScene scene, AIMesh aiMesh, MeshStructure3D... meshStructures) {
        SkeletonData skeletonData = AnimationLoadingUtils.readSkeleton(aiMesh, bonesList);
        if (skeletonData == null) {
            throw new JGemsIOException("Failed to read bones in animated model");
        }
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
        Arrays.asList(meshStructures).stream().filter(Objects::nonNull).forEach(e -> e.loadAnimations(finalAnimations));
        JGemsHelper.getLogger().info("Loaded animation (size:" + animations.size() + ") for: " + this.getPath());
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loaded animation(size:" + animations.size() + ")");
        return skeletonData;
    }


    private MeshGroup processMeshGroup(GameResources gameResources, boolean isAnimated, boolean attachMeshBuffer) {
        MeshGroup meshGroup = new MeshGroup();
        MeshBuffer meshBuffer = attachMeshBuffer ? new MeshBuffer() : null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene aiScene = this.loadAIScene(stack, this.getPath(), isAnimated);
            int totalMaterials = aiScene.mNumMaterials();

            List<Material> materialList = new ArrayList<>();
            List<Bone> bonesList = new ArrayList<>();
            for (int i = 0; i < totalMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(aiScene.mMaterials()).get(i));
                Material material = ModelLoadingUtils.readMaterial(gameResources, aiMaterial, this.getPath().getParentPath());
                if (meshBuffer != null) {
                    gameResources.getResourceArrays().getMeshBuffersDataArray().addMaterial(material);
                }
                materialList.add(material);
            }

            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Building Mesh Group...");

            int totalMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            SkeletonData skeletonData = null;
            if (totalMeshes == 0) {
                throw new JGemsIOException("Caught invalid model: " + this.getPath());
            }
            for (int i = 0; i < totalMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(Objects.requireNonNull(aiMeshes).get(i));
                if (isAnimated) {
                    skeletonData = this.readSkeleton(bonesList, aiScene, aiMesh, meshGroup, meshBuffer);
                }

                int matIdx = aiMesh.mMaterialIndex();
                Material material = matIdx >= 0 && matIdx < materialList.size() ? materialList.get(matIdx) : new Material();

                RenderMesh meshData = this.createRenderMesh(aiMesh, skeletonData);
                meshGroup.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(meshData, material));

                if (meshBuffer != null) {
                    DataMesh meshData2 = this.createDataMesh(aiMesh, skeletonData);
                    meshBuffer.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(meshData2, material));
                    gameResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(meshBuffer);
                    meshGroup.setLinkedMeshBuffer(meshBuffer);
                }
            }
            Assimp.aiReleaseImport(aiScene);

        } catch (Exception e) {
            JGemsHelper.getLogger().error(e.getMessage());
            return null;
        }

        return meshGroup;
    }

    private MeshBuffer processMeshBuffer(GameResources gameResources, boolean isAnimated) {
        MeshBuffer meshBuffer = new MeshBuffer();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene aiScene = this.loadAIScene(stack, this.getPath(), isAnimated);
            int totalMaterials = aiScene.mNumMaterials();

            List<Material> materialList = new ArrayList<>();
            List<Bone> bonesList = new ArrayList<>();
            for (int i = 0; i < totalMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(aiScene.mMaterials()).get(i));
                Material material = ModelLoadingUtils.readMaterial(gameResources, aiMaterial, this.getPath().getParentPath());
                gameResources.getResourceArrays().getMeshBuffersDataArray().addMaterial(material);
                materialList.add(material);
            }

            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Building Mesh Buffer...");

            int totalMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            SkeletonData skeletonData = null;
            if (totalMeshes == 0) {
                throw new JGemsIOException("Caught invalid model: " + this.getPath());
            }
            for (int i = 0; i < totalMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(Objects.requireNonNull(aiMeshes).get(i));
                if (isAnimated) {
                    skeletonData = this.readSkeleton(bonesList, aiScene, aiMesh, meshBuffer);
                }

                int matIdx = aiMesh.mMaterialIndex();
                Material material = matIdx >= 0 && matIdx < materialList.size() ? materialList.get(matIdx) : new Material();

               DataMesh meshData = this.createDataMesh(aiMesh, skeletonData);
               meshBuffer.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(meshData, material));
               gameResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(meshBuffer);
            }
            Assimp.aiReleaseImport(aiScene);

        } catch (Exception e) {
            JGemsHelper.getLogger().error(e.getMessage());
            return null;
        }

        return meshBuffer;
    }

    private DataMesh createDataMesh(AIMesh aiMesh, SkeletonData skeletonData) {
        List<Integer> vertices = ModelLoadingUtils.readVertices(aiMesh);
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

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);
        FloatVertexAttribute vaNormals = new FloatVertexAttribute(DefaultAttributePointers.ATTR_NORMALS);
        FloatVertexAttribute vaTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TANGENTS);
        FloatVertexAttribute vaBiTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BI_TANGENTS);

        renderMesh.putVertexIndexes(vertices);
        vaPositions.put(positions);
        vaNormals.put(normals);
        vaTextureCoordinates.put(textureCoordinates);
        vaTangents.put(tangents);
        vaBiTangents.put(biTangents);

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

    public GameResources getGameResources() {
        return this.gameResources;
    }

    public JGemsPath getPath() {
        return this.path;
    }

    public ResourceCache getResourceCache() {
        return this.getGameResources().getResourceCache();
    }
}