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

package javagems3d.system.resources.assets.shaders.buffers;

import java.nio.ByteBuffer;

public final class ShaderStorageBufferObject {
    private final int binding;
    private final int bufferSize;
    private ByteBuffer mappedBuffer;

    public ShaderStorageBufferObject(int binding) {
        this(binding, -1);
    }

    public ShaderStorageBufferObject(int binding, int bufferSize) {
        this.binding = binding;
        this.bufferSize = bufferSize;
        this.mappedBuffer = null;
    }

    @Override
    public int hashCode() {
        return this.getBinding();
    }

    public ByteBuffer getMappedBuffer() {
        return this.mappedBuffer;
    }

    public void setMappedBuffer(ByteBuffer mappedBuffer) {
        this.mappedBuffer = mappedBuffer;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public int getBinding() {
        return this.binding;
    }
}