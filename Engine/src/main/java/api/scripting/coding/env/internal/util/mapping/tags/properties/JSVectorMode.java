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

package api.scripting.coding.env.internal.util.mapping.tags.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.base.VectorMode;

@JSCodingClass(binding = "JSVectorMode", description = "Vector mode enum.")
public enum JSVectorMode {

    @JSCodingField(description = "2D vector (vec2f)")
    VEC2F(VectorMode.VEC2F),

    @JSCodingField(description = "3D vector (vec3f)")
    VEC3F(VectorMode.VEC3F),

    @JSCodingField(description = "4D vector (vec4f)")
    VEC4F(VectorMode.VEC4F);

    @JSHideFromDoc
    private final VectorMode mode;

    @JSHideFromDoc
    JSVectorMode(VectorMode mode) {
        this.mode = mode;
    }

    @JSHideFromDoc
    public VectorMode getJava() {
        return this.mode;
    }
}