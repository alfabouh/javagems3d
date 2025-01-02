package javagems3d.graphics.rendering.programs.ssbo;

import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46;

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
        shaderStorageBuffers.put(shaderStorageBufferObject, ssboID);
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


    public static ShaderStorageBufferObject SSBO(int id) {
        return new ShaderStorageBufferObject(id);
    }

    public static int getSSBO_ID(ShaderStorageBufferObject shaderStorageBufferObject) {
        return shaderStorageBuffers.get(shaderStorageBufferObject);
    }

    public static void clearAllSSBOs() {
        ShaderStorageBufferProgram.shaderStorageBuffers.values().forEach(GL46::glDeleteBuffers);
        ShaderStorageBufferProgram.shaderStorageBuffers.clear();
    }
}
