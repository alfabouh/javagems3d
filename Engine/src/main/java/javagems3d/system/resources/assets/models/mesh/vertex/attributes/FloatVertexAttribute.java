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

package javagems3d.system.resources.assets.models.mesh.vertex.attributes;

import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;

public final class FloatVertexAttribute extends VertexAttribute<Float> {
    private FloatBuffer floatBuffer;

    public FloatVertexAttribute(RenderAttributePointer attributePointer) {
        super(attributePointer);
    }

    @Override
    public void pushGLBuffer() {
        this.floatBuffer = MemoryUtil.memAllocFloat(this.getValues().size());
        this.floatBuffer.put(JGemsHelper.Math.convertFloatsArray(this.getValues())).flip();
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, (FloatBuffer) this.getBuffer(), GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(this.floatBuffer);
    }

    public FloatVertexAttribute putArray(float[] i) {
        for (float a : i) {
            this.getValues().add(a);
        }
        return this;
    }

    public void clearData() {
        super.clearData();
        this.floatBuffer = null;
    }

    @Override
    public Buffer getBuffer() {
        return this.floatBuffer;
    }

    @Override
    public int attributeType() {
        return GL46.GL_FLOAT;
    }
}
