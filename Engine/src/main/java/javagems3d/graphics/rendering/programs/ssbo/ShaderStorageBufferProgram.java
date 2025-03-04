package javagems3d.graphics.rendering.programs.ssbo;

import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.nio.*;
import java.util.HashMap;
import java.util.Map;

public abstract class ShaderStorageBufferProgram {
    private static final Map<ShaderStorageBufferObject, Integer> shaderStorageBuffers = new HashMap<>();

    public static void mapBuffer(@NotNull ShaderStorageBufferObject shaderStorageBufferObject, int access) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        ByteBuffer buffer = GL46.glMapBufferRange(GL46.GL_SHADER_STORAGE_BUFFER, 0, shaderStorageBufferObject.getBufferSize(), access);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        shaderStorageBufferObject.setMappedBuffer(buffer);
    }

    public static void createSSBOData(@NotNull ShaderStorageBufferObject shaderStorageBufferObject, int usage) {
        if (ShaderStorageBufferProgram.shaderStorageBuffers.containsKey(shaderStorageBufferObject)) {
            throw new JGemsRuntimeException("SSBO-container already keeps buffer with binding: " + shaderStorageBufferObject.getBinding());
        }
        int ssboID = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBufferSize(), usage);
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), ssboID);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        ShaderStorageBufferProgram.shaderStorageBuffers.put(shaderStorageBufferObject, ssboID);
    }

    public static void createSSBOStorage(@NotNull ShaderStorageBufferObject shaderStorageBufferObject, int usage) {
        if (ShaderStorageBufferProgram.shaderStorageBuffers.containsKey(shaderStorageBufferObject)) {
            throw new JGemsRuntimeException("SSBO-container already keeps buffer with binding: " + shaderStorageBufferObject.getBinding());
        }
        int ssboID = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        int bufferSize = shaderStorageBufferObject.getBufferSize();
        GL46.glBufferStorage(GL46.GL_SHADER_STORAGE_BUFFER, bufferSize, usage);
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), ssboID);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        ShaderStorageBufferProgram.shaderStorageBuffers.put(shaderStorageBufferObject, ssboID);
    }

    public static void updateSubDataSSBO(@NotNull ShaderStorageBufferObject shaderStorageBufferObject, long offset, Buffer buffer) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);

        if (buffer instanceof ByteBuffer) {
            GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, (ByteBuffer) buffer);
        } else if (buffer instanceof IntBuffer) {
            GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, (IntBuffer) buffer);
        } else if (buffer instanceof FloatBuffer) {
            GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, (FloatBuffer) buffer);
        } else if (buffer instanceof LongBuffer) {
            GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, (LongBuffer) buffer);
        } else {
            throw new JGemsRuntimeException("Unsupported buffer type: " + buffer.getClass().getName());
        }

        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void clearBufferData(@NotNull ShaderStorageBufferObject shaderStorageBufferObject) {
        GL46.glMemoryBarrier(GL46.GL_SHADER_STORAGE_BARRIER_BIT);
        ByteBuffer buffer = shaderStorageBufferObject.getMappedBuffer();
        if (buffer == null) {
            throw new IllegalStateException("SSBO is not mapped");
        }
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put(i, (byte) 0);
        }
        GL46.glMemoryBarrier(GL46.GL_SHADER_STORAGE_BARRIER_BIT);
    }

    public static ByteBuffer readData(@NotNull ShaderStorageBufferObject shaderStorageBufferObject) {
        GL46.glMemoryBarrier(GL46.GL_SHADER_STORAGE_BARRIER_BIT);
        return shaderStorageBufferObject.getMappedBuffer();
    }

    /*
    public static ByteBuffer readData(ShaderStorageBufferObject shaderStorageBufferObject) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        ByteBuffer buffer = GL46.glMapBuffer(GL46.GL_SHADER_STORAGE_BUFFER, GL46.GL_READ_ONLY);
        GL46.glUnmapBuffer(GL46.GL_SHADER_STORAGE_BUFFER);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        return buffer;
    }
     */

    public static int getSSBO_ID(@NotNull ShaderStorageBufferObject shaderStorageBufferObject) {
        return ShaderStorageBufferProgram.shaderStorageBuffers.get(shaderStorageBufferObject);
    }

    public static void clearAll() {
        ShaderStorageBufferProgram.shaderStorageBuffers.values().forEach(GL46::glDeleteBuffers);
        ShaderStorageBufferProgram.shaderStorageBuffers.clear();
    }
}
