package ru.jgems3d.engine.system.resources.assets.models.loader;

import com.google.common.io.ByteStreams;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.assets.loaders.TextureAssetsLoader;
import ru.jgems3d.engine.system.service.exceptions.JGemsException;
import ru.jgems3d.engine.system.service.exceptions.JGemsIOException;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.material.samples.ColorSample;
import ru.jgems3d.engine.system.resources.assets.material.samples.TextureSample;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.engine.system.resources.manager.GameResources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {
    public static final AIFileReadProc AI_FILE_READ = AIFileReadProc.create((pFile, pBuffer, size, count) -> {
        PointerBuffer meta = getAIFileMeta(pFile);
        long position = meta.get(1);
        long remaining = meta.get(2) - position;
        long requested = size * count;
        long elements = Long.compareUnsigned(requested, remaining) <= 0 ? count : Long.divideUnsigned(remaining, size);
        MemoryUtil.memCopy(meta.get(0) + position, pBuffer, size * elements);
        meta.put(1, position + size * elements);
        return elements;
    });
    public static final AIFileWriteProc AI_FILE_WRITE = AIFileWriteProc.create((pFile, pBuffer, memB, count) -> {
        throw new UnsupportedOperationException();
    });
    public static final AIFileTellProc AI_FILE_TELL = AIFileTellProc.create(pFile -> getAIFileMeta(pFile).get(1));
    public static final AIFileTellProc AI_FILE_SIZE = AIFileTellProc.create(pFile -> getAIFileMeta(pFile).get(2));
    public static final AIFileSeek AI_FILE_SEEK = AIFileSeek.create((pFile, offset, origin) -> {
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
    public static final AIFileFlushProc AI_FILE_FLUSH = AIFileFlushProc.create(pFile -> {
        throw new UnsupportedOperationException();
    });
    public static final AIFileOpenProc AI_FILE_OPEN = AIFileOpenProc.create((pFileIO, fileName, openMode) -> {
        ByteBuffer data;
       try (InputStream inputStream = JGems3D.loadFileFromJar(new JGemsPath(MemoryUtil.memUTF8(fileName)))){
          byte[] stream = ByteStreams.toByteArray(inputStream);
          data = MemoryUtil.memCalloc(stream.length);
          data.put(stream);
          data.flip();
       } catch (IOException e) {
           throw new JGemsIOException(e);
       }
       MemoryStack stack = MemoryStack.stackGet();
       return AIFile.calloc(stack).ReadProc(AI_FILE_READ).WriteProc(AI_FILE_WRITE).TellProc(AI_FILE_TELL).FileSizeProc(AI_FILE_SIZE).SeekProc(AI_FILE_SEEK).FlushProc(AI_FILE_FLUSH).UserData(stack.mallocPointer(3).put(0, MemoryUtil.memAddress(data)).put(1, 0L).put(2, data.remaining()).address()).address();
    });
    public static final AIFileCloseProc AI_FILE_CLOSE = AIFileCloseProc.create((pFileIO, pFile) -> {
        PointerBuffer meta = getAIFileMeta(pFile);
        long dataAddress = meta.get(0);
        if (dataAddress != 0L) {
            MemoryUtil.nmemFree(dataAddress);
            meta.put(0, 0L);
        }
    });

    public static PointerBuffer getAIFileMeta(long pFile) {
        return MemoryUtil.memPointerBuffer(MemoryUtil.memGetAddress(pFile + AIFile.USERDATA), 3);
    }

    // section MeshLoad
    @SuppressWarnings("all")
    private static MeshDataGroup loadMesh(GameResources gameResources, JGemsPath modelPath) {
        JGemsHelper.getLogger().log("Loading model " + modelPath);

        final int FLAGS = Assimp.aiProcess_ImproveCacheLocality | Assimp.aiProcess_OptimizeGraph | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_GenNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_PreTransformVertices;
        MeshDataGroup meshDataGroup = new MeshDataGroup();

        if (JGems3D.checkFileExistsInJar(modelPath)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                AIScene scene = Assimp.aiImportFileEx(modelPath.getFullPath(), FLAGS, AIFileIO.calloc(stack).OpenProc(ModelLoader.AI_FILE_OPEN).CloseProc(ModelLoader.AI_FILE_CLOSE));
                if (scene != null) {
                    int totalMaterials = scene.mNumMaterials();
                    List<Material> materialList = new ArrayList<>();
                    for (int i = 0; i < totalMaterials; i++) {
                        AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(i));
                        materialList.add(ModelLoader.readMaterial(gameResources, aiMaterial, modelPath.getParentPath()));
                    }
                    JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Writing mesh...");
                    int totalMeshes = scene.mNumMeshes();
                    PointerBuffer aiMeshes = scene.mMeshes();
                    for (int i = 0; i < totalMeshes; i++) {
                        AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                        Mesh mesh = ModelLoader.readMesh(aiMesh);
                        int matIdx = aiMesh.mMaterialIndex();
                        Material material = new Material();
                        if (matIdx >= 0 && matIdx < materialList.size()) {
                            material = materialList.get(matIdx);
                        }
                        meshDataGroup.putNode(new ModelNode(mesh, material));
                    }
                    Assimp.aiReleaseImport(scene);
                } else {
                    throw new JGemsRuntimeException("Couldn't create assimp scene!");
                }
            }
        } else {
            JGemsHelper.getLogger().error("Couldn't find " + modelPath);
            return null;
        }
        return meshDataGroup;
    }

    @SuppressWarnings("all")
    public static MeshDataGroup createMesh(GameResources gameResources, JGemsPath path) {
        ResourceCache resourceCache = gameResources.getResourceCache();
        if (resourceCache.checkObjectInCache(path)) {
            return (MeshDataGroup) resourceCache.getCachedObject(path);
        }
        MeshDataGroup meshDataGroup = ModelLoader.loadMesh(gameResources, path);
        resourceCache.addObjectInBuffer(path, meshDataGroup);
        return meshDataGroup;
    }

    private static Mesh readMesh(AIMesh aiMesh) {
        int[] vertices = ModelLoader.readVertices(aiMesh);
        float[] textureCoordinates = ModelLoader.readTextureCoordinates(aiMesh);
        float[] positions = ModelLoader.readPositions(aiMesh);
        float[] normals = ModelLoader.readNormals(aiMesh);
        float[] tangents = ModelLoader.readTangents(aiMesh);
        float[] biTangents = ModelLoader.readBiTangents(aiMesh);

        if (textureCoordinates.length == 0) {
            int totalElements = (positions.length / 3) * 2;
            textureCoordinates = new float[totalElements];
        }

        Mesh mesh = new Mesh();
        mesh.pushIndexes(vertices);
        mesh.pushPositions(positions);
        mesh.pushNormals(normals);
        mesh.pushTextureCoordinates(textureCoordinates);
        mesh.pushTangent(tangents);
        mesh.pushBiTangent(biTangents);
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
            AIVector3D textCd = buffer.get();
            data[pos++] = textCd.x();
            data[pos++] = 1 - textCd.y();
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

    private static float[] readBiTangents(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mBitangents();
        assert buffer != null;
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D biTangent = buffer.get();
            data[pos++] = biTangent.x();
            data[pos++] = biTangent.y();
            data[pos++] = biTangent.z();
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

    // section Material
    private static Material readMaterial(GameResources gameResources, AIMaterial aiMaterial, String fullPath) {
        Material material = new Material();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color4Dd = AIColor4D.create();
            if (Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color4Dd) == Assimp.aiReturn_SUCCESS) {
                material.setDiffuse(ColorSample.createColor(new Vector4f(color4Dd.r(), color4Dd.g(), color4Dd.b(), color4Dd.a())));
            }

            PointerBuffer properties = aiMaterial.mProperties();
            for (int j = 0; j < properties.limit(); j++) {
                AIMaterialProperty property = AIMaterialProperty.create(properties.get(j));
                AIString aiString = property.mKey();
                String s = aiString.dataString();
                if (s.equals(Assimp.AI_MATKEY_OPACITY)) {
                    if (property.mData().remaining() >= 4) {
                        float opacity = property.mData().getFloat(0);
                        material.setFullOpacity(opacity);
                    }
                }
            }

            String emission = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_EMISSIVE);
            String metallic = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_AMBIENT);
            String specular = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_SPECULAR);
            String normals = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_NORMALS);
            String opacity = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_OPACITY);
            String diffuse = ModelLoader.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_DIFFUSE);
            try {
                if (!diffuse.isEmpty()) {
                    TextureSample textureSample = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath(fullPath, diffuse), new TextureSample.Params(true));
                    if (textureSample.isValid()) {
                        material.setDiffuse(textureSample);
                    }
                }
                if (!opacity.isEmpty()) {
                    TextureSample textureSample = gameResources.createTexture(new JGemsPath(fullPath, opacity), new TextureSample.Params(true));
                    if (textureSample.isValid()) {
                        material.setOpacityMap(textureSample);
                    }
                }
                if (!normals.isEmpty()) {
                    TextureSample textureSample = gameResources.createTexture(new JGemsPath(fullPath, normals), new TextureSample.Params(true));
                    if (textureSample.isValid()) {
                        material.setNormalsMap(textureSample);
                    }
                }
                if (!emission.isEmpty()) {
                    TextureSample textureSample = gameResources.createTexture(new JGemsPath(fullPath, emission), new TextureSample.Params(true));
                    if (textureSample.isValid()) {
                        material.setEmissionMap(textureSample);
                    }
                }
                if (!metallic.isEmpty()) {
                    TextureSample textureSample = gameResources.createTexture(new JGemsPath(fullPath, metallic), new TextureSample.Params(true));
                    if (textureSample.isValid()) {
                        material.setMetallicMap(textureSample);
                    }
                }
                if (!specular.isEmpty()) {
                    TextureSample textureSample = gameResources.createTexture(new JGemsPath(fullPath, specular), new TextureSample.Params(true));
                    if (textureSample.isValid()) {
                        material.setSpecularMap(textureSample);
                    }
                }
            } catch (JGemsException e) {
                e.printStackTrace(System.err);
            }
        }
        return material;
    }

    private static String tryReadTexture(MemoryStack memoryStack, AIMaterial aiMaterial, int key) {
        AIString aiTexturePath = AIString.calloc(memoryStack);
        Assimp.aiGetMaterialTexture(aiMaterial, key, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
        return aiTexturePath.dataString();
    }
}
