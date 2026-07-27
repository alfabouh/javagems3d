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

import logger.Log;
import org.lwjgl.opengl.GL46;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class UniformBufferProgram {
    private final int uboBlock;
    private final String name;
    private final int shaderId;
    private int binding;

    public UniformBufferProgram(int shaderId, String name) {
        this.uboBlock = GL46.glGenBuffers();
        this.shaderId = shaderId;
        if (this.getUboBlock() == 0) {
            throw new JGemsRuntimeException("Could not create uniform-buffer program");
        }
        this.name = name;
    }

    public boolean createUniformBuffer(int binding, int bytes) {
        int uniformLocation = this.getLocation();
        this.binding = binding;
        if (uniformLocation < 0) {
            Log.get().warn("Could not find uniform-buffer " + this.getName());
            return false;
        }
        this.setupUniformBuffer(bytes, binding);
        return true;
    }

    private void setupUniformBuffer(int bytes, int binding) {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL46.glBindBufferBase(GL46.GL_UNIFORM_BUFFER, binding, this.getUboBlock());
        GL46.glUniformBlockBinding(this.shaderId, this.getLocation(), this.getBinding());
        GL46.glBufferData(GL46.GL_UNIFORM_BUFFER, bytes, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    public int getLocation() {
        return GL46.glGetUniformBlockIndex(this.shaderId, this.getName());
    }

    public void setUniformBufferData(long offset, ByteBuffer buffer) {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, offset, buffer);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    public void setUniformBufferData(long offset, FloatBuffer buffer) {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, offset, buffer);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    public void setUniformBufferData(long offset, IntBuffer buffer) {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, offset, buffer);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    public void setUniformBufferData(long offset, float[] values) {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, offset, values);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    public int getBinding() {
        return this.binding;
    }

    public String getName() {
        return this.name;
    }

    public int getUboBlock() {
        return this.uboBlock;
    }
}
