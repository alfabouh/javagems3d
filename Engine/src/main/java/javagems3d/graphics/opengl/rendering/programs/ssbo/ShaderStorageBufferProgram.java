package javagems3d.graphics.opengl.rendering.programs.ssbo;

import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public abstract class ShaderStorageBufferProgram {
    public static final Map<ShaderStorageBufferObject, Integer> shaderStorageBuffers = new HashMap<>();

    public static void createSSBO(ShaderStorageBufferObject shaderStorageBufferObject) {
        int ssboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL30.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBufferSize(), GL30.GL_DYNAMIC_DRAW);
        GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), ssboID);
        shaderStorageBuffers.put(shaderStorageBufferObject, ssboID);
    }

    public static void fillSSBOWithData(ShaderStorageBufferObject shaderStorageBufferObject, FloatBuffer floatBuffer) {
        int ssboID = shaderStorageBuffers.get(shaderStorageBufferObject);
        GL30.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL30.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), floatBuffer);
        GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), ssboID);
    }

    public static int getSSBO_ID(ShaderStorageBufferObject shaderStorageBufferObject) {
        return shaderStorageBuffers.get(shaderStorageBufferObject);
    }
}
