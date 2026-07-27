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

import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public abstract class VertexAttribute<T> {
    private final RenderAttributePointer attributePointer;
    private List<T> values;

    public VertexAttribute(RenderAttributePointer attributePointer) {
        this.attributePointer = attributePointer;
        this.values = new ArrayList<>();
    }

    public VertexAttribute<T> set(List<T> values) {
        this.values = values;
        return this;
    }

    public VertexAttribute<T> put(List<T> values) {
        this.getValues().addAll(values);
        return this;
    }

    public VertexAttribute<T> put(T value) {
        this.getValues().add(value);
        return this;
    }

    public abstract void pushGLBuffer();
    public abstract Buffer getBuffer();
    public abstract int attributeType();

    public void clearData() {
        if (this.values != null) {
            this.getValues().clear();
            this.values = null;
        }
    }

    public List<T> getValues() {
        return this.values;
    }

    @Override
    public final int hashCode() {
        return this.getAttributePointer().getIndex();
    }

    public int getIndex() {
        return this.getAttributePointer().getIndex();
    }

    public RenderAttributePointer getAttributePointer() {
        return this.attributePointer;
    }
}
