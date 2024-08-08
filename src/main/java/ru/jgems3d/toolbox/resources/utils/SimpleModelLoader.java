package ru.jgems3d.toolbox.resources.utils;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.service.exceptions.JGemsException;
import ru.jgems3d.engine.system.service.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.materials.samples.ColorSample;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.assets.utils.ModelLoader;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.toolbox.resources.TBoxResourceManager;
import ru.jgems3d.toolbox.resources.samples.TextureSample;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleModelLoader {
    @SuppressWarnings("all")
    private static MeshDataGroup loadMesh(JGPath modelPath) {
        SystemLogging.get().getLogManager().log("Loading model " + modelPath);

        final int FLAGS = Assimp.aiProcess_OptimizeGraph | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_GenNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_PreTransformVertices;
        MeshDataGroup meshDataGroup = new MeshDataGroup();

        if (JGems3D.checkFileInJar(modelPath)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                try (AIScene scene = Assimp.aiImportFileEx(modelPath.getSPath(), FLAGS, AIFileIO.calloc(stack).OpenProc(ModelLoader.AI_FILE_OPEN).CloseProc(ModelLoader.AI_FILE_CLOSE))) {
                    if (scene != null) {
                        int totalMaterials = scene.mNumMaterials();
                        List<Material> materialList = new ArrayList<>();
                        for (int i = 0; i < totalMaterials; i++) {
                            try (AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(scene.mMaterials()).get(i))) {
                                materialList.add(SimpleModelLoader.readMaterial(aiMaterial, modelPath.getParentPath()));
                            }
                        }
                        int totalMeshes = scene.mNumMeshes();
                        PointerBuffer aiMeshes = scene.mMeshes();
                        for (int i = 0; i < totalMeshes; i++) {
                            try (AIMesh aiMesh = AIMesh.create(Objects.requireNonNull(aiMeshes).get(i))) {
                                Mesh mesh = SimpleModelLoader.readMesh(aiMesh);
                                int matIdx = aiMesh.mMaterialIndex();
                                Material material = new Material();
                                if (matIdx >= 0 && matIdx < materialList.size()) {
                                    material = materialList.get(matIdx);
                                }
                                meshDataGroup.putNode(new ModelNode(mesh, material));
                            }
                        }
                    } else {
                        throw new JGemsException();
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
        return meshDataGroup;
    }

    @SuppressWarnings("all")
    public static MeshDataGroup createMesh(ResourceCache resourceCache, JGPath modelPath) {
        if (resourceCache.checkObjectInCache(modelPath)) {
            return (MeshDataGroup) resourceCache.getCachedObject(modelPath);
        }
        MeshDataGroup meshDataGroup = SimpleModelLoader.loadMesh(modelPath);
        resourceCache.addObjectInBuffer(modelPath, meshDataGroup);
        return meshDataGroup;
    }

    private static Mesh readMesh(AIMesh aiMesh) {
        int[] vertices = SimpleModelLoader.readVertices(aiMesh);
        float[] textureCoordinates = SimpleModelLoader.readTextureCoordinates(aiMesh);
        float[] positions = SimpleModelLoader.readPositions(aiMesh);
        float[] normals = SimpleModelLoader.readNormals(aiMesh);

        if (textureCoordinates.length == 0) {
            int totalElements = (positions.length / 3) * 2;
            textureCoordinates = new float[totalElements];
        }

        Mesh mesh = new Mesh();
        mesh.pushIndexes(vertices);
        mesh.pushPositions(positions);
        mesh.pushNormals(normals);
        mesh.pushTextureCoordinates(textureCoordinates);
        mesh.bakeMesh();
        return mesh;
    }

    private static float[] readTextureCoordinates(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = 1 - textCoord.y();
        }
        return data;
    }

    private static float[] readNormals(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mNormals();
        assert buffer != null;
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D normal = buffer.get();
            data[pos++] = normal.x();
            data[pos++] = normal.y();
            data[pos++] = normal.z();
        }
        return data;
    }

    private static int[] readVertices(AIMesh aiMesh) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    private static float[] readPositions(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D position = buffer.get();
            data[pos++] = position.x();
            data[pos++] = position.y();
            data[pos++] = position.z();
        }
        return data;
    }

    private static Material readMaterial(AIMaterial aiMaterial, String fullPath) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color4D = AIColor4D.create();
            if (Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color4D) == Assimp.aiReturn_SUCCESS) {
                material.setDiffuse(ColorSample.createColor(new Vector4f(color4D.r(), color4D.g(), color4D.b(), color4D.a())));
            }
            color4D.clear();
            String diffuse = SimpleModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_DIFFUSE);
            if (diffuse != null) {
                TextureSample textureSample = (TextureSample) TBoxResourceManager.getResource(fullPath + diffuse);
                if (textureSample == null) {
                    textureSample = TBoxResourceManager.createTextureInJar(fullPath + diffuse);
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
