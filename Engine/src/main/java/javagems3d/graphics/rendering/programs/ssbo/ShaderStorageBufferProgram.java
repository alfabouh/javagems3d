package javagems3d.graphics.rendering.programs.ssbo;

import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public abstract class ShaderStorageBufferProgram {
    public static final Map<ShaderStorageBufferObject, Integer> shaderStorageBuffers = new HashMap<>();

    public static void createSSBO(ShaderStorageBufferObject shaderStorageBufferObject) {
        int ssboID = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBufferSize(), GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), ssboID);
        shaderStorageBuffers.put(shaderStorageBufferObject, ssboID);
    }

    public static void fillSSBOWithData(ShaderStorageBufferObject shaderStorageBufferObject, FloatBuffer floatBuffer) {
        int ssboID = shaderStorageBuffers.get(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), floatBuffer);
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), ssboID);
    }

    public static int getSSBO_ID(ShaderStorageBufferObject shaderStorageBufferObject) {
        return shaderStorageBuffers.get(shaderStorageBufferObject);
    }
}
