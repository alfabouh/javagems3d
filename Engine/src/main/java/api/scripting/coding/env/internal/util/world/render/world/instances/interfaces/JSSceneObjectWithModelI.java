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

package api.scripting.coding.env.internal.util.world.render.world.instances.interfaces;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.bound.JSBoxAABB;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;

@JSCodingClass(binding = "JSSceneObjectWithModelI", description = "Interface for objects that have 3D model and animation control")
public interface JSSceneObjectWithModelI extends JSAnimatedObjectI {

    @JSCodingFunctionOrMethod(description = "Get underlying Java modeled object")
    IModeled getJavaModeledObject();

    @JSCodingFunctionOrMethod(description = "Get model of the object")
    default JSModel3D getModel() {
        return new JSModel3D(this.getJavaModeledObject().getModel());
    }

    @JSCodingFunctionOrMethod(description = "Check if object has valid model")
    default boolean hasModel() {
        return this.getJavaModeledObject().hasModel();
    }

    @JSCodingFunctionOrMethod(description = "Update animation state")
    default void updateAnimation() {
        this.getJavaModeledObject().updateAnimation();
    }

    @JSCodingFunctionOrMethod(description = "Get bounding box from mesh or animation")
    default JSBoxAABB getAABB() {
        CullingAABB aabb = this.getJavaModeledObject().pickAABBDataFromMesh();
        return aabb == null ? null : new JSBoxAABB(aabb);
    }
}