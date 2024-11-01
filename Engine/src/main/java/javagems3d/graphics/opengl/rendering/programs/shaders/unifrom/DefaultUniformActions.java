package javagems3d.graphics.opengl.rendering.programs.shaders.unifrom;

import org.joml.*;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public abstract class DefaultUniformActions {
    public static UniformProgram.UniformAction INTEGER(int i) {
        return e -> {
            GL30.glUniform1i(e, i);
            return true;
        };
    }

    public static UniformProgram.UniformAction FLOAT(float f) {
        return e -> {
            GL30.glUniform1f(e, f);
            return true;
        };
    }

    public static UniformProgram.UniformAction VEC2F(Vector2f value) {
        return e -> {
            GL30.glUniform2f(e, value.x, value.y);
            return true;
        };
    }

    public static UniformProgram.UniformAction VEC3F(Vector3f value) {
        return e -> {
            GL30.glUniform3f(e, value.x, value.y, value.z);
            return true;
        };
    }

    public static UniformProgram.UniformAction VEC4F(Vector4f value) {
        return e -> {
            GL30.glUniform4f(e, value.x, value.y, value.z, value.w);
            return true;
        };
    }

    public static UniformProgram.UniformAction VEC2I(Vector2i value) {
        return e -> {
            GL30.glUniform2f(e, value.x, value.y);
            return true;
        };
    }

    public static UniformProgram.UniformAction VEC3I(Vector3i value) {
        return e -> {
            GL30.glUniform3f(e, value.x, value.y, value.z);
            return true;
        };
    }

    public static UniformProgram.UniformAction VEC4I(Vector4i value) {
        return e -> {
            GL30.glUniform4f(e, value.x, value.y, value.z, value.w);
            return true;
        };
    }

    public static UniformProgram.UniformAction BOOLEAN(boolean value) {
        return e -> {
            GL30.glUniform1i(e, value ? 1 : 0);
            return true;
        };
    }

    public static UniformProgram.UniformAction MAT4F(Matrix4f value) {
        return e -> {
            try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                FloatBuffer floatBuffer = memoryStack.mallocFloat(16);
                value.get(floatBuffer);
                GL20.glUniformMatrix4fv(e, false, floatBuffer);
            }
            return true;
        };
    }

    public static UniformProgram.UniformAction MAT3F(Matrix3f value) {
        return e -> {
            try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                FloatBuffer floatBuffer = memoryStack.mallocFloat(12);
                value.get(floatBuffer);
                GL20.glUniformMatrix3fv(e, false, floatBuffer);
            }
            return true;
        };
    }
}
