package ru.BouH.engine.game.resources.assets.utils;

import org.joml.Vector4d;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.materials.textures.ColorSample;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.assets.models.mesh.ModelNode;
import ru.BouH.engine.game.resources.cache.GameCache;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelLoader {

    @SuppressWarnings("all")
    private static MeshDataGroup loadMesh(String modelPath) {
        Game.getGame().getLogManager().debug("Loading model " + modelPath);
        StringBuilder absolutePath = new StringBuilder();
        absolutePath.append(Game.getGamePath());
        absolutePath.append(modelPath);

        final int FLAGS = Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_PreTransformVertices;
        MeshDataGroup meshDataGroup = new MeshDataGroup();

        File file1 = new File(absolutePath.toString());

        if (new File(file1.getAbsolutePath()).exists()) {
            try (AIScene scene = Assimp.aiImportFile(absolutePath.toString(), FLAGS)) {
                if (scene != null) {
                    int totalMaterials = scene.mNumMaterials();
                    List<Material> materialList = new ArrayList<>();
                    for (int i = 0; i < totalMaterials; i++) {
                        try (AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(scene.mMaterials()).get(i))) {
                            materialList.add(ModelLoader.readMaterial(aiMaterial, file1.getParent()));
                        }
                    }
                    int totalMeshes = scene.mNumMeshes();
                    PointerBuffer aiMeshes = scene.mMeshes();
                    for (int i = 0; i < totalMeshes; i++) {
                        try (AIMesh aiMesh = AIMesh.create(Objects.requireNonNull(aiMeshes).get(i))) {
                            Mesh mesh = ModelLoader.readMesh(aiMesh);
                            int matIdx = aiMesh.mMaterialIndex();
                            Material material = new Material();
                            if (matIdx >= 0 && matIdx < materialList.size()) {
                                material = materialList.get(matIdx);
                            }
                            meshDataGroup.putNode(new ModelNode(mesh, material));
                        }
                    }
                } else {
                    throw new RuntimeException();
                }
            } catch (RuntimeException e) {
                Game.getGame().getLogManager().error("Error, while loading " + modelPath);
                return null;
            }
        } else {
            Game.getGame().getLogManager().warn("Couldn't find " + modelPath);
            return null;
        }
        return meshDataGroup;
    }

    @SuppressWarnings("all")
    public static MeshDataGroup createMesh(GameCache gameCache, String modelPath) {
        if (gameCache.checkObjectInCache(modelPath)) {
            return gameCache.getCachedMeshDataGroup(modelPath);
        }
        MeshDataGroup meshDataGroup = ModelLoader.loadMesh(modelPath);
        gameCache.addObjectInBuffer(modelPath, meshDataGroup);
        return meshDataGroup;
    }

    private static Mesh readMesh(AIMesh aiMesh) {
        int[] vertices = ModelLoader.readVertices(aiMesh);
        float[] textureCoordinates = ModelLoader.readTextureCoordinates(aiMesh);
        float[] positions = ModelLoader.readPositions(aiMesh);
        float[] normals = ModelLoader.readNormals(aiMesh);
        float[] tangents = ModelLoader.readTangents(aiMesh);
        float[] bitangents = ModelLoader.readBitangents(aiMesh);

        if (textureCoordinates.length == 0) {
            int totalElements = (positions.length / 3) * 2;
            textureCoordinates = new float[totalElements];
        }
        Mesh mesh = new Mesh();
        mesh.putIndexValues(vertices);
        mesh.putPositionValues(positions);
        mesh.putNormalValues(normals);
        mesh.putTextureCoordinateValues(textureCoordinates);
        mesh.putTangentValues(tangents);
        mesh.putBitangentValues(bitangents);
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

    private static float[] readTangents(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTangents();
        assert buffer != null;
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D tangent = buffer.get();
            data[pos++] = tangent.x();
            data[pos++] = tangent.y();
            data[pos++] = tangent.z();
        }
        return data;
    }

    private static float[] readBitangents(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mBitangents();
        assert buffer != null;
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D bitangent = buffer.get();
            data[pos++] = bitangent.x();
            data[pos++] = bitangent.y();
            data[pos++] = bitangent.z();
        }
        return data;
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
                material.setDiffuse(ColorSample.createColor(new Vector4d(color4D.r(), color4D.g(), color4D.b(), color4D.a())));
            }
            color4D.clear();
            String diffuse = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_DIFFUSE);
            String emissive = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_EMISSIVE);
            String metallic = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_AMBIENT);
            String specular = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_SPECULAR);
            String normals = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_NORMALS);
            if (diffuse != null) {
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + "\\" + diffuse);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTextureOutsideJar(fullPath + "\\" + diffuse);
                }
                material.setDiffuse(textureSample);
            }
            if (normals != null) {
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + "\\" + normals);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTextureOutsideJar(fullPath + "\\" + normals);
                }
                material.setNormals(textureSample);
            }
            if (emissive != null) {
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + "\\" + emissive);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTextureOutsideJar(fullPath + "\\" + emissive);
                }
                material.setEmissive(textureSample);
            }
            if (metallic != null) {
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + "\\" + metallic);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTextureOutsideJar(fullPath + "\\" + metallic);
                }
                material.setMetallic(textureSample);
            }
            if (specular != null) {
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + "\\" + specular);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTextureOutsideJar(fullPath + "\\" + specular);
                }
                material.setSpecular(textureSample);
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
