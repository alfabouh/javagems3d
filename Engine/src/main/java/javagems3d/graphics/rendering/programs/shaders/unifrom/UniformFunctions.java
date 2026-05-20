package javagems3d.graphics.rendering.programs.shaders.unifrom;

import org.joml.*;
import org.lwjgl.opengl.ARBBindlessTexture;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public abstract class UniformFunctions {
    public static UniformProgram.UFunction UINTEGER(int i) {
        return e -> {
            GL46.glUniform1ui(e, i);
            return true;
        };
    }

    public static UniformProgram.UFunction INTEGER(int i) {
        return e -> {
            GL46.glUniform1i(e, i);
            return true;
        };
    }

    public static UniformProgram.UFunction FLOAT(float f) {
        return e -> {
            GL46.glUniform1f(e, f);
            return true;
        };
    }

    public static UniformProgram.UFunction VEC2F(Vector2f value) {
        return e -> {
            GL46.glUniform2f(e, value.x, value.y);
            return true;
        };
    }

    public static UniformProgram.UFunction VEC3F(Vector3f value) {
        return e -> {
            GL46.glUniform3f(e, value.x, value.y, value.z);
            return true;
        };
    }

    public static UniformProgram.UFunction VEC4F(Vector4f value) {
        return e -> {
            GL46.glUniform4f(e, value.x, value.y, value.z, value.w);
            return true;
        };
    }

    public static UniformProgram.UFunction VEC2I(Vector2i value) {
        return e -> {
            GL46.glUniform2f(e, value.x, value.y);
            return true;
        };
    }

    public static UniformProgram.UFunction VEC2UI(long value) {
        return e -> {
            int low = (int) (value & 0xFFFFFFFFL);
            int high = (int) ((value >> 32) & 0xFFFFFFFFL);
            GL46.glUniform2ui(e, low, high);
            return true;
        };
    }

    public static UniformProgram.UFunction TEXTURE64ARB(long handler) {
        return e -> {
            ARBBindlessTexture.glUniformHandleui64ARB(e, handler);
            return true;
        };
    }

    public static UniformProgram.UFunction VEC3I(Vector3i value) {
        return e -> {
            GL46.glUniform3f(e, value.x, value.y, value.z);
            return true;
        };
    }

    public static UniformProgram.UFunction VEC4I(Vector4i value) {
        return e -> {
            GL46.glUniform4f(e, value.x, value.y, value.z, value.w);
            return true;
        };
    }

    public static UniformProgram.UFunction BOOLEAN(boolean value) {
        return e -> {
            GL46.glUniform1i(e, value ? 1 : 0);
            return true;
        };
    }

    public static UniformProgram.UFunction MAT4F(Matrix4f value) {
        return e -> {
            try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                FloatBuffer floatBuffer = memoryStack.mallocFloat(16);
                value.get(floatBuffer);
                GL46.glUniformMatrix4fv(e, false, floatBuffer);
            }
            return true;
        };
    }

    public static UniformProgram.UFunction MAT3F(Matrix3f value) {
        return e -> {
            try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                FloatBuffer floatBuffer = memoryStack.mallocFloat(12);
                value.get(floatBuffer);
                GL46.glUniformMatrix3fv(e, false, floatBuffer);
            }
            return true;
        };
    }
}
