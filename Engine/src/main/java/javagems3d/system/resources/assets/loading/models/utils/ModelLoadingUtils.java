package javagems3d.system.resources.assets.loading.models.utils;

import com.google.common.io.ByteStreams;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.Color4Texture;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class ModelLoadingUtils {
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
        try (InputStream inputStream = JGems3D.loadFileFromJar(new JGemsPath(MemoryUtil.memUTF8(fileName)))) {
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

    public static List<Float> readTextureCoordinates(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) {
            return new ArrayList<>();
        }
        List<Float> data = new ArrayList<>();
        while (buffer.remaining() > 0) {
            AIVector3D textCd = buffer.get();
            data.add(textCd.x());
            data.add(1 - textCd.y());
        }
        return data;
    }

    public static List<Integer> readVertices(AIMesh aiMesh) {
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
        return indices;
    }

    public static List<Float> readNormals(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mNormals();
        assert buffer != null;
        List<Float> data = new ArrayList<>();
        while (buffer.remaining() > 0) {
            AIVector3D normal = buffer.get();
            data.add(normal.x());
            data.add(normal.y());
            data.add(normal.z());
        }
        return data;
    }

    public static List<Float> readTangents(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTangents();
        if (buffer == null) {
            return new ArrayList<>();
        }
        List<Float> data = new ArrayList<>();
        while (buffer.remaining() > 0) {
            AIVector3D tangent = buffer.get();
            data.add(tangent.x());
            data.add(tangent.y());
            data.add(tangent.z());
        }
        return data;
    }

    public static List<Float> readBiTangents(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mBitangents();
        if (buffer == null) {
            return new ArrayList<>();
        }
        List<Float> data = new ArrayList<>();
        while (buffer.remaining() > 0) {
            AIVector3D biTangent = buffer.get();
            data.add(biTangent.x());
            data.add(biTangent.y());
            data.add(biTangent.z());
        }
        return data;
    }

    public static List<Float> readPositions(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        List<Float> data = new ArrayList<>();
        while (buffer.remaining() > 0) {
            AIVector3D position = buffer.get();
            data.add(position.x());
            data.add(position.y());
            data.add(position.z());
        }
        return data;
    }

    // section Material
    public static Material readMaterial(@Nullable JGemsShaderManager computeTransparentPixels, SystemResources systemResources, AIMaterial aiMaterial, String fullPath) {
        float opacityConstant = 1.0f;
        boolean textureIsImageAndHasAlphaPixels = false;
        ISample diffuseSample = null;
        ITexture2DProgram emissionSample = null;
        ITexture2DProgram metallicSample = null;
        ITexture2DProgram specularSample = null;
        ITexture2DProgram normalsSample = null;
        ITexture2DProgram opacitySample = null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color4Dd = AIColor4D.create();
            if (Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color4Dd) == Assimp.aiReturn_SUCCESS) {
                diffuseSample = new Color4Texture(color4Dd.r(), color4Dd.g(), color4Dd.b(), color4Dd.a());
            }

            PointerBuffer properties = aiMaterial.mProperties();
            for (int j = 0; j < properties.limit(); j++) {
                AIMaterialProperty property = AIMaterialProperty.create(properties.get(j));
                AIString aiString = property.mKey();
                String s = aiString.dataString();
                if (s.equals(Assimp.AI_MATKEY_OPACITY)) {
                    if (property.mData().remaining() >= 4) {
                        opacityConstant = property.mData().getFloat(0);
                    }
                }
            }

            String emission = ModelLoadingUtils.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_EMISSIVE);
            String metallic = ModelLoadingUtils.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_AMBIENT);
            String specular = ModelLoadingUtils.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_SPECULAR);
            String normals = ModelLoadingUtils.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_NORMALS);
            String opacity = ModelLoadingUtils.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_OPACITY);
            String diffuse = ModelLoadingUtils.tryReadTexture(stack, aiMaterial, Assimp.aiTextureType_DIFFUSE);

            final ImageTexture.Properties imageProperties = new ImageTexture.Properties(true, true);
            try {
                if (!diffuse.isEmpty()) {
                    ITexture2DProgram textureSample = systemResources.createTexture(ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(fullPath, diffuse), imageProperties);
                    if (textureSample.isValid()) {
                        if (computeTransparentPixels != null) {
                            textureIsImageAndHasAlphaPixels = Material.Transparency.scanForAlphaPixels(computeTransparentPixels, textureSample);
                        }
                        diffuseSample = textureSample;
                    }
                }
                if (!opacity.isEmpty()) {
                    ITexture2DProgram textureSample = systemResources.createTexture(null, new JGemsPath(fullPath, opacity), imageProperties);
                    if (textureSample.isValid()) {
                        opacitySample = textureSample;
                    }
                }
                if (!normals.isEmpty()) {
                    ITexture2DProgram textureSample = systemResources.createTexture(null, new JGemsPath(fullPath, normals), imageProperties);
                    if (textureSample.isValid()) {
                        normalsSample = textureSample;
                    }
                }
                if (!emission.isEmpty()) {
                    ITexture2DProgram textureSample = systemResources.createTexture(null, new JGemsPath(fullPath, emission), imageProperties);
                    if (textureSample.isValid()) {
                        emissionSample = textureSample;
                    }
                }
                if (!metallic.isEmpty()) {
                    ITexture2DProgram textureSample = systemResources.createTexture(null, new JGemsPath(fullPath, metallic), imageProperties);
                    if (textureSample.isValid()) {
                        metallicSample = textureSample;
                    }
                }
                if (!specular.isEmpty()) {
                    ITexture2DProgram textureSample = systemResources.createTexture(null, new JGemsPath(fullPath, specular), imageProperties);
                    if (textureSample.isValid()) {
                        specularSample = textureSample;
                    }
                }
            } catch (JGemsException e) {
                Log.get().exception(e);
            }
        }
        Material material = new Material(diffuseSample == null ? ResourceManager.DEFAULT_TEXTURE() : diffuseSample, opacitySample, normalsSample, emissionSample, specularSample, metallicSample);
        material.getTransparency().setHasTransparentPixels(textureIsImageAndHasAlphaPixels);
        material.getTransparency().setOpacity(opacityConstant);
        return material;
    }

    public static String tryReadTexture(MemoryStack memoryStack, AIMaterial aiMaterial, int key) {
        AIString aiTexturePath = AIString.calloc(memoryStack);
        Assimp.aiGetMaterialTexture(aiMaterial, key, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
        return aiTexturePath.dataString();
    }
}
