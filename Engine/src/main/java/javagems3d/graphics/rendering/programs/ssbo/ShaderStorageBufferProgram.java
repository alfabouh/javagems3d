package javagems3d.graphics.rendering.programs.ssbo;

import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.*;
import java.util.HashMap;
import java.util.Map;

public abstract class ShaderStorageBufferProgram {
    private static final Map<ShaderStorageBufferObject, Integer> shaderStorageBuffers = new HashMap<>();

    public static void createSSBO(ShaderStorageBufferObject shaderStorageBufferObject) {
        if (ShaderStorageBufferProgram.shaderStorageBuffers.containsKey(shaderStorageBufferObject)) {
            throw new JGemsRuntimeException("SSBO-container already keeps buffer with binding: " + shaderStorageBufferObject.getBinding());
        }
        int ssboID = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBufferSize(), GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), ssboID);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        ShaderStorageBufferProgram.shaderStorageBuffers.put(shaderStorageBufferObject, ssboID);
    }

    public static void fillSSBOWithData(ShaderStorageBufferObject shaderStorageBufferObject, long offset, Buffer buffer) {
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
            throw new IllegalArgumentException("Unsupported buffer type: " + buffer.getClass().getName());
        }

        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void zeroIntBuffer(ShaderStorageBufferObject shaderStorageBufferObject) {
        int bufferSize = shaderStorageBufferObject.getBufferSize();
        ByteBuffer zeroBuffer = (ByteBuffer) MemoryUtil.memAlloc(bufferSize).clear();
        zeroBuffer.put(new byte[bufferSize]).flip();

        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, 0, zeroBuffer);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);

        MemoryUtil.memFree(zeroBuffer);
    }

    public static ByteBuffer readData(ShaderStorageBufferObject shaderStorageBufferObject) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        ByteBuffer buffer = GL46.glMapBuffer(GL46.GL_SHADER_STORAGE_BUFFER, GL46.GL_READ_ONLY);
        GL46.glUnmapBuffer(GL46.GL_SHADER_STORAGE_BUFFER);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        return buffer;
    }

    public static int getSSBO_ID(ShaderStorageBufferObject shaderStorageBufferObject) {
        return ShaderStorageBufferProgram.shaderStorageBuffers.get(shaderStorageBufferObject);
    }

    public static void clearAll() {
        ShaderStorageBufferProgram.shaderStorageBuffers.values().forEach(GL46::glDeleteBuffers);
        ShaderStorageBufferProgram.shaderStorageBuffers.clear();
    }
}
