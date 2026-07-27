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

@SuppressWarnings("all")
public abstract class DefaultAttributePointers {
    public static final RenderAttributePointer ATTR_POSITIONS = new RenderAttributePointer(0, 3, Float.BYTES, new Float(0));
    public static final RenderAttributePointer ATTR_TEXTURE_COORDINATES = new RenderAttributePointer(1, 2, Float.BYTES, new Float(0));
    public static final RenderAttributePointer ATTR_NORMALS = new RenderAttributePointer(2, 3, Float.BYTES, new Float(0));
    public static final RenderAttributePointer ATTR_TANGENTS = new RenderAttributePointer(3, 3, Float.BYTES, new Float(0));
    public static final RenderAttributePointer ATTR_BI_TANGENTS = new RenderAttributePointer(4, 3, Float.BYTES, new Float(0));

    public static final RenderAttributePointer ATTR_BONES_INDEXES = new RenderAttributePointer(5, 4, Integer.BYTES, new Integer(0));
    public static final RenderAttributePointer ATTR_BONES_WEIGHTS = new RenderAttributePointer(6, 4, Float.BYTES, new Float(0));
}