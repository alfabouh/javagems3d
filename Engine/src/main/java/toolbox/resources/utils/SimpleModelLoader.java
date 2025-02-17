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

package toolbox.resources.utils;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.loading.models.utils.ModelLoadingUtils;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.service.exceptions.JGemsNullException;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import javagems3d.JGems3D;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import logger.SystemLogging;
import toolbox.resources.TBoxResourceManager;
import toolbox.resources.samples.ImageTexture;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleModelLoader {
    @SuppressWarnings("all")
    private static MeshGroup loadMesh(TBoxResourceManager tBoxResourceManager, JGemsPath modelPath) {
        SystemLogging.get().getLogManager().trace("Loading model " + modelPath);

        final int FLAGS = Assimp.aiProcess_OptimizeGraph | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_GenNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_PreTransformVertices;
        MeshGroup meshGroup = new MeshGroup();

        if (JGems3D.checkFileExistsInJar(modelPath)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                try (AIScene scene = Assimp.aiImportFileEx(modelPath.getFullPath(), FLAGS, AIFileIO.calloc(stack).OpenProc(ModelLoadingUtils.AI_FILE_OPEN).CloseProc(ModelLoadingUtils.AI_FILE_CLOSE))) {
                    if (scene != null) {
                        int totalMaterials = scene.mNumMaterials();
                        List<Material> materialList = new ArrayList<>();
                        for (int i = 0; i < totalMaterials; i++) {
                            try (AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(scene.mMaterials()).get(i))) {
                                materialList.add(SimpleModelLoader.readMaterial(tBoxResourceManager, aiMaterial, modelPath.getParentPath()));
                            }
                        }
                        int totalMeshes = scene.mNumMeshes();
                        PointerBuffer aiMeshes = scene.mMeshes();
                        for (int i = 0; i < totalMeshes; i++) {
                            try (AIMesh aiMesh = AIMesh.create(Objects.requireNonNull(aiMeshes).get(i))) {
                                RenderMesh renderMesh = SimpleModelLoader.readMesh(aiMesh);
                                int matIdx = aiMesh.mMaterialIndex();
                                Material material = new Material();
                                if (matIdx >= 0 && matIdx < materialList.size()) {
                                    material = materialList.get(matIdx);
                                }
                                meshGroup.putNode(0, new MeshNode3D<RenderMesh>(renderMesh, material));
                            }
                        }
                    } else {
                        throw new JGemsNullException("Couldn't create assimp scene");
                    }
                } catch (RuntimeException e) {
                    SystemLogging.get().getLogManager().error("Error, while loading " + modelPath);
                    e.printStackTrace();
                    return null;
                }
            }
        } else {
            SystemLogging.get().getLogManager().error("Couldn't find " + modelPath);
            return null;
        }
        return meshGroup;
    }

    @SuppressWarnings("all")
    public static MeshGroup createMesh(TBoxResourceManager tBoxResourceManager, JGemsPath modelPath) {
        if (tBoxResourceManager.getCache().checkObjectInCache(modelPath)) {
            return (MeshGroup) tBoxResourceManager.getCache().getCachedObject(modelPath);
        }
        MeshGroup meshGroup = SimpleModelLoader.loadMesh(tBoxResourceManager, modelPath);
        tBoxResourceManager.getCache().addObjectInBuffer(modelPath, meshGroup);
        return meshGroup;
    }

    private static RenderMesh readMesh(AIMesh aiMesh) {
        List<Integer> vertices = ModelLoadingUtils.readVertices(aiMesh);
        List<Float> textureCoordinates = ModelLoadingUtils.readTextureCoordinates(aiMesh);
        List<Float> positions = ModelLoadingUtils.readPositions(aiMesh);
        List<Float> normals = ModelLoadingUtils.readNormals(aiMesh);
        List<Float> tangents = ModelLoadingUtils.readTangents(aiMesh);
        List<Float> biTangents = ModelLoadingUtils.readBiTangents(aiMesh);

        RenderMesh renderMesh = new RenderMesh();

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);
        FloatVertexAttribute vaNormals = new FloatVertexAttribute(DefaultAttributePointers.ATTR_NORMALS);

        renderMesh.putVertexIndexes(vertices);
        vaPositions.put(positions);
        vaNormals.put(normals);
        vaTextureCoordinates.put(textureCoordinates);

        renderMesh.putVertexAttribute(vaPositions);
        renderMesh.putVertexAttribute(vaTextureCoordinates);
        renderMesh.putVertexAttribute(vaNormals);

        renderMesh.bakeMesh();
        return renderMesh;
    }

    private static Material readMaterial(TBoxResourceManager tBoxResourceManager, AIMaterial aiMaterial, String fullPath) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color4D = AIColor4D.create();
            if (Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color4D) == Assimp.aiReturn_SUCCESS) {
                material.setDiffuse(new RGBAColor(new Vector4f(color4D.r(), color4D.g(), color4D.b(), color4D.a())));
            }
            color4D.clear();
            String diffuse = SimpleModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_DIFFUSE);
            if (diffuse != null) {
                ImageTexture textureSample = (ImageTexture) tBoxResourceManager.getResource(fullPath + diffuse);
                if (textureSample == null) {
                    textureSample = tBoxResourceManager.createTexture(fullPath + diffuse);
                }
                if (textureSample.isValid()) {
                    material.setDiffuse(textureSample);
                } else {
                    material.setDefaultDiffuse();
                }
            }
        }
        return material;
    }

    private static String tryReadTexture(MemoryStack memoryStack, AIMaterial aiMaterial, int key) {
        AIString aiTexturePath = AIString.calloc(memoryStack);
        Assimp.aiGetMaterialTexture(aiMaterial, key, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
        String texturePath = aiTexturePath.dataString();
        if (!texturePath.isEmpty()) {
            return texturePath;
        }
        return null;
    }
}
