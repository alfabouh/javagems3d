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

package javagems3d.system.resources.assets.models.mesh.vertex.pointers;

public final class RenderAttributePointer {
    private final int index;
    private final int length;
    private final int bytes;
    private final boolean normalized;
    private final int stride;
    private final int pointer;
    private final Number defaultVal;

    public RenderAttributePointer(int index, int length, int bytes, Number defaultVal) {
        this(index, length, bytes, false, 0, 0, defaultVal);
    }

    public RenderAttributePointer(int index, int lengthInMemory, int bytes, boolean normalized, int stride, int pointer, Number defaultVal) {
        this.bytes = bytes;
        this.index = index;
        this.length = lengthInMemory;
        this.normalized = normalized;
        this.stride = stride;
        this.pointer = pointer;
        this.defaultVal = defaultVal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Integer) {
            return (Integer) obj == this.getIndex();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.getIndex();
    }

    public Number getDefaultVal() {
        return this.defaultVal;
    }

    public int getBytes() {
        return this.bytes;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLengthInMemory() {
        return this.length;
    }

    public boolean isNormalized() {
        return this.normalized;
    }

    public int getStride() {
        return this.stride;
    }

    public int getPointer() {
        return this.pointer;
    }
}
