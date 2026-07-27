/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
