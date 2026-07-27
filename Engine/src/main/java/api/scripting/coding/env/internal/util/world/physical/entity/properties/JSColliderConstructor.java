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

package api.scripting.coding.env.internal.util.world.physical.entity.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import javagems3d.physics.colliders.IColliderConstructor;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;

@JSCodingClass(binding = "JSColliderConstructor", description = "Factory for collision shapes")
public class JSColliderConstructor {
    private final IColliderConstructor constructor;

    public static IColliderConstructor getDynamic(JSMeshStructure3D meshStructure) {
        return new MeshCollider(meshStructure.getJavaMeshStructure3D(), true);
    }

    public static IColliderConstructor getStatic(JSMeshStructure3D meshStructure) {
        return new MeshCollider(meshStructure.getJavaMeshStructure3D(), false);
    }

    public static IColliderConstructor get(JSMeshStructure3D meshStructure, boolean isBodyDynamic) {
        return new MeshCollider(meshStructure.getJavaMeshStructure3D(), isBodyDynamic);
    }

    public JSColliderConstructor(IColliderConstructor constructor) {
        this.constructor = constructor;
    }

    @JSCodingFunctionOrMethod(description = "Create collider")
    public Object create() {
        return this.constructor.execute();
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public IColliderConstructor getJavaConstructor() {
        return this.constructor;
    }
}