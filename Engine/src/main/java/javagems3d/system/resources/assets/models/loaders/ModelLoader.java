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

package javagems3d.system.resources.assets.models.loaders;

import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.material.IndirectMeshesMaterialsTable;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.animation.components.Bone;
import javagems3d.system.resources.assets.models.loaders.utils.AnimationLoadingUtils;
import javagems3d.system.resources.assets.models.loaders.utils.ModelLoadingUtils;
import javagems3d.system.resources.assets.models.mesh.DirectRenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.IntegerVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;

import java.util.*;

public abstract class ModelLoader {

    private static MeshBuffer loadMesh(GameResources gameResources, JGemsPath modelPath) {
        return ModelLoader.loadMesh(gameResources, modelPath, false);
    }

    // section MeshLoad
    @SuppressWarnings("all")
    private static MeshBuffer loadMesh(GameResources gameResources, JGemsPath modelPath, boolean isAnimated) {
        JGemsHelper.getLogger().log("Loading model " + modelPath);

        int FLAGS = Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_ImproveCacheLocality | Assimp.aiProcess_OptimizeGraph | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_GenNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace;
        if (!isAnimated) {
            FLAGS |= (Assimp.aiProcess_PreTransformVertices);
        }

        MeshBuffer meshStructure = new MeshBuffer();
        if (JGems3D.checkFileExistsInJar(modelPath)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                AIScene scene = Assimp.aiImportFileEx(modelPath.getFullPath(), FLAGS, AIFileIO.calloc(stack).OpenProc(ModelLoadingUtils.AI_FILE_OPEN).CloseProc(ModelLoadingUtils.AI_FILE_CLOSE));
                if (scene != null) {
                    int totalMaterials = scene.mNumMaterials();
                    List<Material> materialList = new ArrayList<>();
                    List<Bone> bonesList = new ArrayList<>();

                    for (int i = 0; i < totalMaterials; i++) {
                        AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(i));
                        Material material = ModelLoadingUtils.readMaterial(gameResources, aiMaterial, modelPath.getParentPath());
                        materialList.add(material);
                        gameResources.getMaterialsTable().addMaterial(material);
                    }

                    JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Writing mesh...");
                    int totalMeshes = scene.mNumMeshes();
                    PointerBuffer aiMeshes = scene.mMeshes();
                    SkeletonData skeletonData = null;
                    for (int i = 0; i < totalMeshes; i++) {
                        AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                        if (isAnimated) {
                            skeletonData = AnimationLoadingUtils.readSkeleton(aiMesh, bonesList);
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
                            JGemsHelper.getLogger().log("Loaded animation (size:" + animations.size() + ")  for: " + modelPath);
                        }

                        DirectRenderMesh directRenderMesh = ModelLoader.readMesh(aiMesh, skeletonData);

                        int matIdx = aiMesh.mMaterialIndex();
                        Material material = new Material();
                        int matId = IndirectMeshesMaterialsTable.START_IDX;
                        if (matIdx >= 0 && matIdx < materialList.size()) {
                            matId = materialList.get(matIdx).getId();
                        }
                        meshStructure.putMeshNode(new MeshBuffer.MeshNode(directRenderMesh, matId));
                    }
                    Assimp.aiReleaseImport(scene);
                } else {
                    throw new JGemsIOException("Couldn't create assimp scene for model: " + modelPath);
                }
            }
        } else {
            JGemsHelper.getLogger().error("Couldn't find " + modelPath);
            return null;
        }
        return meshStructure;
    }

    public static MeshBuffer createMesh(GameResources gameResources, JGemsPath path) {
        return ModelLoader.createMesh(gameResources, path);
    }

    @SuppressWarnings("all")
    public static MeshBuffer createMesh(GameResources gameResources, JGemsPath path, boolean isAnimated) {
        ResourceCache resourceCache = gameResources.getResourceCache();
        if (resourceCache.checkObjectInCache(path)) {
            return (MeshBuffer) resourceCache.getCachedObject(path);
        }
        MeshBuffer meshGroup = ModelLoader.loadMesh(gameResources, path, isAnimated);
        resourceCache.addObjectInBuffer(path, meshGroup);
        return meshGroup;
    }

    private static DirectRenderMesh readMesh(AIMesh aiMesh) {
        return ModelLoader.readMesh(aiMesh, null);
    }

    private static DirectRenderMesh readMesh(AIMesh aiMesh, SkeletonData skeletonData) {
        int[] vertices = ModelLoadingUtils.readVertices(aiMesh);
        float[] textureCoordinates = ModelLoadingUtils.readTextureCoordinates(aiMesh);
        float[] positions = ModelLoadingUtils.readPositions(aiMesh);
        float[] normals = ModelLoadingUtils.readNormals(aiMesh);
        float[] tangents = ModelLoadingUtils.readTangents(aiMesh);
        float[] biTangents = ModelLoadingUtils.readBiTangents(aiMesh);

        if (textureCoordinates.length == 0) {
            int totalElements = (positions.length / 3) * 2;
            textureCoordinates = new float[totalElements];
        }

        DirectRenderMesh directRenderMesh = new DirectRenderMesh();

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);
        FloatVertexAttribute vaNormals = new FloatVertexAttribute(DefaultAttributePointers.ATTR_NORMALS);
        FloatVertexAttribute vaTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TANGENTS);
        FloatVertexAttribute vaBiTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BI_TANGENTS);

        directRenderMesh.putVertexIndexes(vertices);
        vaPositions.putArray(positions);
        vaNormals.putArray(normals);
        vaTextureCoordinates.putArray(textureCoordinates);
        vaTangents.putArray(tangents);
        vaBiTangents.putArray(biTangents);

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
}
