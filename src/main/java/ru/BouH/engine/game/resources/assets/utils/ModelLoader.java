package ru.BouH.engine.game.resources.assets.utils;

import com.google.common.io.ByteStreams;
import org.joml.Vector4d;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class ModelLoader {
    private static PointerBuffer getAIFileMeta(long pFile) { return MemoryUtil.memPointerBuffer(MemoryUtil.memGetAddress(pFile + AIFile.USERDATA), 3); }

    private static final AIFileReadProc AI_FILE_READ = AIFileReadProc.create((pFile, pBuffer, size, count) -> {
        PointerBuffer meta = getAIFileMeta(pFile);
        long position = meta.get(1);
        long remaining = meta.get(2) - position;
        long requested = size * count;
        long elements = Long.compareUnsigned(requested, remaining) <= 0 ? count : Long.divideUnsigned(remaining, size);
        MemoryUtil.memCopy(meta.get(0) + position, pBuffer, size * elements);
        meta.put(1, position + size * elements);
        return elements;
    });

    private static final AIFileWriteProc AI_FILE_WRITE = AIFileWriteProc.create((pFile, pBuffer, memB, count) -> {
        throw new UnsupportedOperationException();
    });

    private static final AIFileTellProc AI_FILE_TELL = AIFileTellProc.create(pFile -> getAIFileMeta(pFile).get(1));

    private static final AIFileTellProc AI_FILE_SIZE = AIFileTellProc.create(pFile -> getAIFileMeta(pFile).get(2));

    private static final AIFileSeek AI_FILE_SEEK = AIFileSeek.create((pFile, offset, origin) -> {
        PointerBuffer meta = getAIFileMeta(pFile);
        long limit = meta.get(2);
        long position;
        switch (origin) {
            case Assimp.aiOrigin_SET:
                position = offset;
                break;
            case Assimp.aiOrigin_CUR:
                position = meta.get(1) + offset;
                break;
            case Assimp.aiOrigin_END:
                position = limit - offset;
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (position < 0 || limit < position) {
            return -1;
        }
        meta.put(1, position);
        return 0;
    });

    private static final AIFileFlushProc AI_FILE_FLUSH = AIFileFlushProc.create(pFile -> { throw new UnsupportedOperationException(); });

    private static final AIFileOpenProc AI_FILE_OPEN = AIFileOpenProc.create((pFileIO, fileName, openMode) -> {
        ByteBuffer data;
        try {
            byte[] stream = ByteStreams.toByteArray(Game.loadFileJar(MemoryUtil.memUTF8(fileName)));
            data = MemoryUtil.memAlloc(stream.length);
            data.put(stream);
            data.flip();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MemoryStack stack = MemoryStack.stackGet();
        return AIFile.calloc(stack).ReadProc(AI_FILE_READ).WriteProc(AI_FILE_WRITE).TellProc(AI_FILE_TELL).FileSizeProc(AI_FILE_SIZE).SeekProc(AI_FILE_SEEK).FlushProc(AI_FILE_FLUSH).UserData(stack.mallocPointer(3).put(0, MemoryUtil.memAddress(data)).put(1, 0L).put(2, data.remaining()).address()).address();
    });

    private static final AIFileCloseProc AI_FILE_CLOSE = AIFileCloseProc.create((pFileIO, pFile) -> {});

    @SuppressWarnings("all")
    private static MeshDataGroup loadMesh(String modelPath, String modelName) {
        Game.getGame().getLogManager().debug("Loading model " + modelPath + modelName);

        final int FLAGS = Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_PreTransformVertices;
        MeshDataGroup meshDataGroup = new MeshDataGroup();

        if (Game.seekInJar(modelPath + modelName)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                try (AIScene scene = Assimp.aiImportFileEx(modelPath + modelName, FLAGS, AIFileIO.calloc(stack).OpenProc(ModelLoader.AI_FILE_OPEN).CloseProc(ModelLoader.AI_FILE_CLOSE))) {
                    if (scene != null) {
                        int totalMaterials = scene.mNumMaterials();
                        List<Material> materialList = new ArrayList<>();
                        for (int i = 0; i < totalMaterials; i++) {
                            try (AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(scene.mMaterials()).get(i))) {
                                materialList.add(ModelLoader.readMaterial(aiMaterial, modelPath));
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
            }
        } else {
            Game.getGame().getLogManager().warn("Couldn't find " + modelPath);
            return null;
        }
        return meshDataGroup;
    }

    @SuppressWarnings("all")
    public static MeshDataGroup createMesh(GameCache gameCache, String modelPath, String modelName) {
        if (gameCache.checkObjectInCache(modelPath + modelName)) {
            return gameCache.getCachedMeshDataGroup(modelPath + modelName);
        }
        MeshDataGroup meshDataGroup = ModelLoader.loadMesh(modelPath, modelName);
        gameCache.addObjectInBuffer(modelPath + modelName, meshDataGroup);
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
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + diffuse);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTexture(fullPath + diffuse);
                }
                if (textureSample.isValid()) {
                    material.setDiffuse(textureSample);
                } else {
                    material.setDefaultDiffuse();
                }
            }
            if (normals != null) {
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + normals);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTexture(fullPath + normals);
                }
                if (textureSample.isValid()) {
                    material.setNormals(textureSample);
                }
            }
            if (emissive != null) {
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + emissive);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTexture(fullPath + emissive);
                }
                if (textureSample.isValid()) {
                    material.setEmissive(textureSample);
                }
            }
            if (metallic != null) {
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + metallic);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTexture(fullPath + metallic);
                }
                if (textureSample.isValid()) {
                    material.setMetallic(textureSample);
                }
            }
            if (specular != null) {
                TextureSample textureSample = ResourceManager.getTextureResource(fullPath + specular);
                if (textureSample == null) {
                    textureSample = ResourceManager.createTexture(fullPath + specular);
                }
                if (textureSample.isValid()) {
                    material.setSpecular(textureSample);
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
