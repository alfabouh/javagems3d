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

package javagems3d.graphics.rendering.programs.ssbo;

import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.*;
import java.util.HashMap;
import java.util.Map;

public abstract class ShaderStorageBufferProgram {
    private static final Map<ShaderStorageBufferObject, Integer> shaderStorageBuffers = new HashMap<>();

    public static void mapBuffer(@NotNull ShaderStorageBufferObject shaderStorageBufferObject, int access) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        ByteBuffer buffer = GL46.glMapBufferRange(GL46.GL_SHADER_STORAGE_BUFFER, 0, shaderStorageBufferObject.getBufferSize(), access);
        if (buffer == null) {
            throw new JGemsRuntimeException("Couldn't create SSBO Mapping");
        }
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        shaderStorageBufferObject.setMappedBuffer(buffer);
    }

    public static void createSSBOData(@NotNull ShaderStorageBufferObject shaderStorageBufferObject, int usage) {
        if (ShaderStorageBufferProgram.shaderStorageBuffers.containsKey(shaderStorageBufferObject)) {
            throw new JGemsRuntimeException("SSBO-container already keeps buffer with binding: " + shaderStorageBufferObject.getBinding());
        }
        int ssboID = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBufferSize(), usage);
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), ssboID);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        ShaderStorageBufferProgram.shaderStorageBuffers.put(shaderStorageBufferObject, ssboID);
    }

    public static void createSSBOStorage(@NotNull ShaderStorageBufferObject shaderStorageBufferObject, int flags) {
        if (ShaderStorageBufferProgram.shaderStorageBuffers.containsKey(shaderStorageBufferObject)) {
            throw new JGemsRuntimeException("SSBO-container already keeps buffer with binding: " + shaderStorageBufferObject.getBinding());
        }
        int ssboID = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        int bufferSize = shaderStorageBufferObject.getBufferSize();
        GL46.glBufferStorage(GL46.GL_SHADER_STORAGE_BUFFER, bufferSize, flags);
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, shaderStorageBufferObject.getBinding(), ssboID);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        ShaderStorageBufferProgram.shaderStorageBuffers.put(shaderStorageBufferObject, ssboID);
    }

    public static void updateSubDataSSBO(@NotNull ShaderStorageBufferObject shaderStorageBufferObject, long offset, Buffer buffer) {
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
            throw new JGemsRuntimeException("Unsupported buffer type: " + buffer.getClass().getName());
        }

        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void clearBufferData(@NotNull ShaderStorageBufferObject shaderStorageBufferObject, int internalFormat, int format, int type, @Nullable ByteBuffer data) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glClearBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, internalFormat, 0L, shaderStorageBufferObject.getBufferSize(), format, type, data);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static ByteBuffer readData(@NotNull ShaderStorageBufferObject shaderStorageBufferObject) {
        if (shaderStorageBufferObject.getMappedBuffer() == null) {
            int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
            GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
            ByteBuffer buffer = MemoryUtil.memAlloc(shaderStorageBufferObject.getBufferSize());
            GL46.glGetBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, 0, buffer);
            GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
            return buffer;
        }
        return shaderStorageBufferObject.getMappedBuffer();
    }

    public static void flush(@NotNull ShaderStorageBufferObject shaderStorageBufferObject) {
        int ssboID = ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, ssboID);
        GL46.glFlushMappedBufferRange(GL46.GL_SHADER_STORAGE_BUFFER, 0, shaderStorageBufferObject.getBufferSize());
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static int getSSBO_ID(@NotNull ShaderStorageBufferObject shaderStorageBufferObject) {
        return ShaderStorageBufferProgram.shaderStorageBuffers.get(shaderStorageBufferObject);
    }

    public static void clear(ShaderStorageBufferObject shaderStorageBufferObject) {
        GL46.glDeleteBuffers(ShaderStorageBufferProgram.getSSBO_ID(shaderStorageBufferObject));
        ShaderStorageBufferProgram.shaderStorageBuffers.remove(shaderStorageBufferObject);
    }

    public static void clearAll() {
        ShaderStorageBufferProgram.shaderStorageBuffers.values().forEach(GL46::glDeleteBuffers);
        ShaderStorageBufferProgram.shaderStorageBuffers.clear();
    }
}