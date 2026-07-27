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
import java.nio.IntBuffer;

public final class IntegerVertexAttribute extends VertexAttribute<Integer> {
    private IntBuffer intBuffer;

    public IntegerVertexAttribute(RenderAttributePointer attributePointer) {
        super(attributePointer);
    }

    @Override
    public void pushGLBuffer() {
        this.intBuffer = MemoryUtil.memAllocInt(this.getValues().size());
        this.intBuffer.put(JGemsHelper.Math.convertIntsArray(this.getValues())).flip();
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, (IntBuffer) this.getBuffer(), GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(this.intBuffer);
    }

    public void clearData() {
        super.clearData();
        this.intBuffer = null;
    }

    public void putArray(int[] i) {
        for (int a : i) {
            this.getValues().add(a);
        }
    }

    @Override
    public Buffer getBuffer() {
        return this.intBuffer;
    }

    @Override
    public int attributeType() {
        return GL46.GL_FLOAT;
    }
}
