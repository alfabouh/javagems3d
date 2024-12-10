package javagems3d.graphics.rendering.programs.ssbo;

import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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

    public static void fillSSBOWithData(ShaderStorageBufferObject shaderStorageBufferObject, long offset, ByteBuffer buffer) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, buffer);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void fillSSBOWithData(ShaderStorageBufferObject shaderStorageBufferObject, long offset, IntBuffer intBuffer) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, intBuffer);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void fillSSBOWithData(ShaderStorageBufferObject shaderStorageBufferObject, long offset, FloatBuffer floatBuffer) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, floatBuffer);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static ShaderStorageBufferObject SSBO_TEMPLATE(int id) {
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
