/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.rendering.programs.shaders.unifroms;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.shaders.base.UniformString;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class UniformProgram {
    private final int programId;
    private final Map<UniformString, Integer> uniforms;

    public UniformProgram(int programId) {
        this.programId = programId;
        this.uniforms = new HashMap<>();
    }

    public boolean createUniform(UniformString uniformName) {
        int uniformLocation = GL20.glGetUniformLocation(this.programId, uniformName.toString());
        this.getUniforms().put(uniformName, uniformLocation);
        return uniformLocation >= 0;
    }

    public boolean setUniform(@NotNull UniformString uniformName, @NotNull Object value) {
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

    public void setUniform(UniformString uniformName, Matrix4f value) {
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = memoryStack.mallocFloat(16);
            value.get(floatBuffer);
            GL20.glUniformMatrix4fv(this.getUniforms().get(uniformName), false, floatBuffer);
        }
    }

    public void setUniform(UniformString uniformName, Vector2f value) {
        GL20.glUniform2f(this.getUniforms().get(uniformName), value.x, value.y);
    }

    public void setUniform(UniformString uniformName, Vector4f value) {
        GL20.glUniform4f(this.getUniforms().get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(UniformString uniformName, Vector3f value) {
        GL20.glUniform3f(this.getUniforms().get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(UniformString uniformName, int value) {
        GL20.glUniform1i(this.getUniforms().get(uniformName), value);
    }

    public void setUniform(UniformString uniformName, float value) {
        Integer a = this.getUniforms().get(uniformName);
        if (a == null) {
            JGemsHelper.getLogger().warn("Uniform " + uniformName + " doesn't located in shader!");
        } else {
            GL20.glUniform1f(this.getUniforms().get(uniformName), value);
        }
    }

    public Map<UniformString, Integer> getUniforms() {
        return this.uniforms;
    }
}
