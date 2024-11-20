package javagems3d.system.resources.assets.models.loaders;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.material.IndirectMeshesMaterialsTable;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.components.Bone;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.loaders.utils.AnimationLoadingUtils;
import javagems3d.system.resources.assets.models.loaders.utils.ModelLoadingUtils;
import javagems3d.system.resources.assets.models.mesh.DirectRenderMesh;
import javagems3d.system.resources.assets.models.mesh.IndirectRenderMesh;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.IntegerVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.path.JGemsPath;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

public class ModelMeshLoader {
    public static final IndirectMeshesMaterialsTable indirectMeshesMaterialsTable = new IndirectMeshesMaterialsTable();
    private final JGemsPath path;

    public ModelMeshLoader(JGemsPath modelPath) {
        this.path = modelPath;
    }

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

        return skeletonData;
    }

    @SuppressWarnings("all")
    public MeshGroup createDirectMeshStructure(GameResources gameResources, boolean isAnimated) {
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
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Writing mesh...");
            int totalMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            SkeletonData skeletonData = null;
            for (int i = 0; i < totalMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                if (isAnimated) {
                    skeletonData = this.readSkeleton(meshStructure, aiScene, aiMesh);
                }
                DirectRenderMesh meshData = this.createDirectMeshData(aiMesh, skeletonData);

                int matIdx = aiMesh.mMaterialIndex();
                Material material = new Material();
                if (matIdx >= 0 && matIdx < materialList.size()) {
                    material = materialList.get(matIdx);
                }
                meshStructure.putMeshNode(new MeshGroup.MeshNode(meshData, material));
            }
            Assimp.aiReleaseImport(aiScene);

        } catch (Exception e) {
            JGemsHelper.getLogger().error(e.getMessage());
            return null;
        }

        return meshStructure;
    }

    @SuppressWarnings("all")
    public MeshBuffer createIndirectMeshStructure(GameResources gameResources, boolean isAnimated) {
        MeshBuffer meshStructure = new MeshBuffer();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene aiScene = this.loadAIScene(stack, this.getPath(), isAnimated);
            int totalMaterials = aiScene.mNumMaterials();
            List<Material> materialList = new ArrayList<>();

            for (int i = 0; i < totalMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
                Material material = ModelLoadingUtils.readMaterial(gameResources, aiMaterial, this.getPath().getParentPath());
                materialList.add(material);
                ModelMeshLoader.indirectMeshesMaterialsTable.addMaterial(material);
            }
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Writing mesh...");
            int totalMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            SkeletonData skeletonData = null;
            for (int i = 0; i < totalMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                if (isAnimated) {
                    skeletonData = this.readSkeleton(meshStructure, aiScene, aiMesh);
                }
                IndirectRenderMesh meshData = this.createIndirectMeshData(aiMesh, skeletonData);

                int matIdx = aiMesh.mMaterialIndex();
                int matId = IndirectMeshesMaterialsTable.START_IDX;
                if (matIdx >= 0 && matIdx < materialList.size()) {
                    matId = materialList.get(matIdx).getId();
                }
                meshStructure.putMeshNode(new MeshBuffer.MeshNode(meshData, matId));
            }
            Assimp.aiReleaseImport(aiScene);

        } catch (Exception e) {
            JGemsHelper.getLogger().error(e.getMessage());
            return null;
        }

        return meshStructure;
    }

    private IndirectRenderMesh createIndirectMeshData(AIMesh aiMesh, SkeletonData skeletonData) {
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

        IndirectRenderMesh indirectRenderMesh = new IndirectRenderMesh();
        indirectRenderMesh.putIndexes(vertices);
        indirectRenderMesh.putBufferInMeshData(DefaultAttributePointers.INDIR_ATTR_POSITIONS, positions);
        indirectRenderMesh.putBufferInMeshData(DefaultAttributePointers.INDIR_ATTR_TEXTURE_COORDINATES, textureCoordinates);
        indirectRenderMesh.putBufferInMeshData(DefaultAttributePointers.INDIR_ATTR_POSITIONS, normals);
        indirectRenderMesh.putBufferInMeshData(DefaultAttributePointers.INDIR_ATTR_TANGENTS, tangents);
        indirectRenderMesh.putBufferInMeshData(DefaultAttributePointers.INDIR_ATTR_BI_TANGENTS, biTangents);

        if (skeletonData != null) {
            IntegerVertexAttribute boneIndexes = new IntegerVertexAttribute(DefaultAttributePointers.ATTR_BONE_INDEXES);
            FloatVertexAttribute boneWeights = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BONE_WEIGHTS);

            boneIndexes.putArray(skeletonData.getBoneIds());
            boneWeights.putArray(skeletonData.getWeights());

           // directMeshData.addVertexAttributeInMesh(boneIndexes);
           // directMeshData.addVertexAttributeInMesh(boneWeights);
        }

        return indirectRenderMesh;
    }

    private DirectRenderMesh createDirectMeshData(AIMesh aiMesh, SkeletonData skeletonData) {
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

        DirectRenderMesh directRenderMesh = new DirectRenderMesh();

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);
        FloatVertexAttribute vaNormals = new FloatVertexAttribute(DefaultAttributePointers.ATTR_NORMALS);
        FloatVertexAttribute vaTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TANGENTS);
        FloatVertexAttribute vaBiTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BI_TANGENTS);

        directRenderMesh.putVertexIndexes(vertices);
        vaPositions.put(positions);
        vaNormals.put(normals);
        vaTextureCoordinates.put(textureCoordinates);
        vaTangents.put(tangents);
        vaBiTangents.put(biTangents);

        directRenderMesh.addVertexAttributeInMesh(vaPositions);
        directRenderMesh.addVertexAttributeInMesh(vaTextureCoordinates);
        directRenderMesh.addVertexAttributeInMesh(vaNormals);
        directRenderMesh.addVertexAttributeInMesh(vaTangents);
        directRenderMesh.addVertexAttributeInMesh(vaBiTangents);

        if (skeletonData != null) {
            IntegerVertexAttribute boneIndexes = new IntegerVertexAttribute(DefaultAttributePointers.ATTR_BONE_INDEXES);
            FloatVertexAttribute boneWeights = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BONE_WEIGHTS);

            boneIndexes.putArray(skeletonData.getBoneIds());
            boneWeights.putArray(skeletonData.getWeights());

            directRenderMesh.addVertexAttributeInMesh(boneIndexes);
            directRenderMesh.addVertexAttributeInMesh(boneWeights);
        }

        directRenderMesh.bakeMesh();
        return directRenderMesh;
    }

    public JGemsPath getPath() {
        return this.path;
    }
}
