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

package api.scripting.coding.env.internal.util.world.render.data;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;

@JSCodingClass(binding = "JSMeshStructureConstructor", description = "Functional interface for constructing mesh structure from input data")
@FunctionalInterface
public interface JSMeshStructureConstructor<T> {
    @JSCodingFunctionOrMethod(description = "Construct mesh structure from input")
    JSMeshStructure3D construct(T data);

    @JSHideFromDoc
    default IModelConstructor<T, IMesh> toJavaConstructor() {
        return (t) -> {
            MeshStructure3D<?> mesh = this.construct(t).getJavaMeshStructure3D();
            @SuppressWarnings("unchecked")
            MeshStructure3D<IMesh> castedMesh = (MeshStructure3D<IMesh>) mesh;
            return castedMesh;
        };
    }
}