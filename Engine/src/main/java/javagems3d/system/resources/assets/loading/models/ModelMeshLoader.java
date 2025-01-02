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
import javagems3d.system.resources.assets.models.mesh.structures.MeshDataType;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.IntegerVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.resources.manager.mesh.MeshBuffersDrawCache;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

public class ModelMeshLoader implements ILoadingHelper {
    private final JGemsPath path;
    private final GameResources gameResources;

    public ModelMeshLoader(@NotNull GameResources gameResources, @NotNull JGemsPath modelPath) {
        this.path = modelPath;
        this.gameResources = gameResources;
    }

    public MeshGroup createMeshGroup(int Flags) {
        boolean animated = (Flags & FLAGS.LOAD_ANIMATIONS) != 0;
        boolean createCollision = (Flags & FLAGS.CREATE_COLLISION_UD) != 0;
        boolean loadInIndirectBuffer = (Flags & FLAGS.LOAD_IN_INDIRECT_BUFFER) != 0;
        MeshGroup meshGroup = null;
        String grString = this.getStr(MeshDataType.GROUP);
        String bffString = this.getStr(MeshDataType.BUFFER);
        if (this.getResourceCache().checkObjectInCache(grString)) {
            meshGroup = this.getResourceCache().getCachedObjectUnSafeCast(grString);
        } else {
            meshGroup = this.processMeshGroup(this.getGameResources(), animated);
            this.getResourceCache().addObjectInBuffer(grString, meshGroup);
        }
        if (meshGroup == null) {
            throw new JGemsNullException("There was an error, while loading the model!");
        }
        if (!this.getResourceCache().checkObjectInCache(bffString)) {
            if (loadInIndirectBuffer) {
                MeshBuffer meshBuffer = this.processMeshBuffer(this.getGameResources(), animated, true);
                if (meshBuffer == null) {
                    throw new JGemsNullException("There was an error, while loading the model!");
                }
                this.getResourceCache().addObjectInBuffer(bffString, meshBuffer);
            }
        }
        if (createCollision) {
            JGemsHelper.UTILS.createMeshCollisionData(meshGroup);
        }
        return meshGroup;
    }

    public MeshBuffer createMeshBuffer(int Flags) {
        boolean animated = (Flags & FLAGS.LOAD_ANIMATIONS) != 0;
        boolean createCollision = (Flags & FLAGS.CREATE_COLLISION_UD) != 0;
        boolean loadInIndirectBuffer = (Flags & ~FLAGS.LOAD_IN_INDIRECT_BUFFER) == 0;
        String bffString = this.getStr(MeshDataType.BUFFER);
        MeshBuffer meshBuffer = null;
        if (this.isCacheValid() && this.getResourceCache().checkObjectInCache(bffString)) {
            meshBuffer = this.getResourceCache().getCachedObjectUnSafeCast(bffString);
        } else {
            meshBuffer = this.processMeshBuffer(this.getGameResources(), animated, loadInIndirectBuffer);
            this.getResourceCache().addObjectInBuffer(bffString, meshBuffer);
        }
        if (meshBuffer == null) {
            throw new JGemsNullException("There was an error, while loading the model!");
        }
        if (createCollision) {
            JGemsHelper.UTILS.createMeshCollisionData(meshBuffer);
        }
        return meshBuffer;
    }

    public String getStr(MeshDataType dataType) {
        return ModelMeshLoader.getModelStr(this.getPath(), dataType);
    }

    public static String getModelStr(JGemsPath path, MeshDataType dataType) {
        return path + dataType.getSuffix();
    }

    //++++++++++++++++++++++++++

    private AIScene loadAIScene(MemoryStack stack, JGemsPath path, boolean isAnimated) {
        if (JGems3D.checkFileExistsInJar(path)) {
            JGemsHelper.getLogger().log("Loading model " + path);

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
    private SkeletonData readSkeleton(MeshStructure meshStructure, AIScene scene, AIMesh aiMesh) {
        List<Bone> bonesList = new ArrayList<>();
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

        meshStructure.loadAnimations(animations);
        JGemsHelper.getLogger().log("Loaded animation (size:" + animations.size() + ")  for: " + this.getPath());
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loaded animation(size:" + animations.size() + ")");

        return skeletonData;
    }

    @SuppressWarnings("all")
    private MeshBuffer processMeshBuffer(GameResources gameResources, boolean isAnimated, boolean loadInIndirectBuffer) {
        MeshBuffer meshStructure = new MeshBuffer();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene aiScene = this.loadAIScene(stack, this.getPath(), isAnimated);
            int totalMaterials = aiScene.mNumMaterials();
            List<Material> materialList = new ArrayList<>();

            for (int i = 0; i < totalMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
                Material material = ModelLoadingUtils.readMaterial(gameResources, aiMaterial, this.getPath().getParentPath());
                material.setId(gameResources.getDataMeshArray().getMaterials().size());
                materialList.add(material);
                if (loadInIndirectBuffer) {
                    gameResources.getDataMeshArray().putMaterial(material);
                }
            }

            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Building MeshBuffer...");
            int totalMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            SkeletonData skeletonData = null;
            for (int i = 0; i < totalMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                if (isAnimated) {
                    skeletonData = this.readSkeleton(meshStructure, aiScene, aiMesh);
                }
                DataMesh meshData = this.createDataMesh(aiMesh, skeletonData);

                int matIdx = aiMesh.mMaterialIndex();
                int matId = MeshBuffersDrawCache.MAT_START_IDX;
                if (matIdx >= 0 && matIdx < materialList.size()) {
                    matId = materialList.get(matIdx).getId();
                }

                meshStructure.putMeshNode(new MeshBuffer.MeshBufferNode(meshData, matId));
            }
            Assimp.aiReleaseImport(aiScene);

        } catch (Exception e) {
            JGemsHelper.getLogger().error(e.getMessage());
            return null;
        }

        if (loadInIndirectBuffer) {
            gameResources.getDataMeshArray().putMeshBuffer(meshStructure);
        }

        return meshStructure;
    }

    @SuppressWarnings("all")
    private MeshGroup processMeshGroup(GameResources gameResources, boolean isAnimated) {
        MeshGroup meshStructure = new MeshGroup();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene aiScene = this.loadAIScene(stack, this.getPath(), isAnimated);
            int totalMaterials = aiScene.mNumMaterials();
            List<Material> materialList = new ArrayList<>();

            for (int i = 0; i < totalMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
                Material material = ModelLoadingUtils.readMaterial(gameResources, aiMaterial, this.getPath().getParentPath());
                materialList.add(material);
            }

            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Building MeshGroup...");
            int totalMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            SkeletonData skeletonData = null;
            for (int i = 0; i < totalMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                if (isAnimated) {
                    skeletonData = this.readSkeleton(meshStructure, aiScene, aiMesh);
                }
                RenderMesh meshData = this.createRenderMesh(aiMesh, skeletonData);

                int matIdx = aiMesh.mMaterialIndex();
                Material material = new Material();
                if (matIdx >= 0 && matIdx < materialList.size()) {
                    material = materialList.get(matIdx);
                }
                meshStructure.putMeshNode(new MeshGroup.MeshGroupNode(meshData, material));
            }
            Assimp.aiReleaseImport(aiScene);

        } catch (Exception e) {
            JGemsHelper.getLogger().error(e.getMessage());
            return null;
        }

        return meshStructure;
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
        dataMesh.putIndexes(vertices);
        dataMesh.putBufferInMeshData(DefaultAttributePointers.ATTR_POSITIONS, positions);
        dataMesh.putBufferInMeshData(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, textureCoordinates);
        dataMesh.putBufferInMeshData(DefaultAttributePointers.ATTR_NORMALS, normals);
        dataMesh.putBufferInMeshData(DefaultAttributePointers.ATTR_TANGENTS, tangents);
        dataMesh.putBufferInMeshData(DefaultAttributePointers.ATTR_BI_TANGENTS, biTangents);

        if (skeletonData != null) {
            IntegerVertexAttribute boneIndexes = new IntegerVertexAttribute(DefaultAttributePointers.ATTR_BONE_INDEXES);
            FloatVertexAttribute boneWeights = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BONE_WEIGHTS);

            boneIndexes.putArray(skeletonData.getBoneIds());
            boneWeights.putArray(skeletonData.getWeights());

           // directMeshData.addVertexAttributeInMesh(boneIndexes);
           // directMeshData.addVertexAttributeInMesh(boneWeights);
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

        renderMesh.addVertexAttributeInMesh(vaPositions);
        renderMesh.addVertexAttributeInMesh(vaTextureCoordinates);
        renderMesh.addVertexAttributeInMesh(vaNormals);
        renderMesh.addVertexAttributeInMesh(vaTangents);
        renderMesh.addVertexAttributeInMesh(vaBiTangents);

        if (skeletonData != null) {
            IntegerVertexAttribute boneIndexes = new IntegerVertexAttribute(DefaultAttributePointers.ATTR_BONE_INDEXES);
            FloatVertexAttribute boneWeights = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BONE_WEIGHTS);

            boneIndexes.putArray(skeletonData.getBoneIds());
            boneWeights.putArray(skeletonData.getWeights());

            renderMesh.addVertexAttributeInMesh(boneIndexes);
            renderMesh.addVertexAttributeInMesh(boneWeights);
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

    public static class FLAGS {
        public static final int
                CREATE_COLLISION_UD = 1 << 2,
                LOAD_ANIMATIONS = 1 << 3,
                LOAD_IN_INDIRECT_BUFFER = 1 << 4;

        public static final int DEFAULT = 0x0;
        public static final int ALL = FLAGS.LOAD_IN_INDIRECT_BUFFER | FLAGS.CREATE_COLLISION_UD | FLAGS.LOAD_ANIMATIONS;
    }
}
