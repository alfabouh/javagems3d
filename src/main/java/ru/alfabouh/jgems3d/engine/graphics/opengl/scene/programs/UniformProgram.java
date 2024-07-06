package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.programs;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class UniformProgram {
    private final int programId;
    private final Map<String, Integer> uniforms;

    public UniformProgram(int programId) {
        this.programId = programId;
        this.uniforms = new HashMap<>();
    }

    public boolean createUniform(String uniformName) {
        int uniformLocation = GL20.glGetUniformLocation(this.programId, uniformName);
        this.uniforms.put(uniformName, uniformLocation);
        return uniformLocation >= 0;
    }

    public boolean setUniform(String uniformName, Object value) {
        if (value instanceof Vector2f) {
            this.setUniform(uniformName, (Vector2f) value);
            return true;
        } else if (value instanceof Matrix4f) {
            this.setUniform(uniformName, (Matrix4f) value);
            return true;
        } else if (value instanceof Vector3f) {
            this.setUniform(uniformName, (Vector3f) value);
            return true;
        } else if (value instanceof Integer) {
            this.setUniform(uniformName, (int) value);
            return true;
        } else if (value instanceof Float) {
            this.setUniform(uniformName, (float) value);
            return true;
        } else if (value instanceof Vector4f) {
            this.setUniform(uniformName, (Vector4f) value);
            return true;
        } else if (value instanceof Boolean) {
            this.setUniform(uniformName, ((Boolean) value) ? 1 : 0);
            return true;
        }
        return false;
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = memoryStack.mallocFloat(16);
            value.get(floatBuffer);
            GL20.glUniformMatrix4fv(this.uniforms.get(uniformName), false, floatBuffer);
        }
    }

    public void setUniform(String uniformName, Vector2f value) {
        GL20.glUniform2f(this.uniforms.get(uniformName), value.x, value.y);
    }

    public void setUniform(String uniformName, Vector4f value) {
        GL20.glUniform4f(this.uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, Vector3f value) {
        GL20.glUniform3f(this.uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(this.uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        Integer a = this.uniforms.get(uniformName);
        if (a == null) {
            SystemLogging.get().getLogManager().warn("Uniform " + uniformName + " doesn't located in shader!");
        } else {
            GL20.glUniform1f(this.uniforms.get(uniformName), value);
        }
    }
}
