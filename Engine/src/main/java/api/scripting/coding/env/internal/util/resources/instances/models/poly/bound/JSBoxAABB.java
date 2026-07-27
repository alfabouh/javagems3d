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

package api.scripting.coding.env.internal.util.resources.instances.models.poly.bound;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSMatrix4f;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSBoxAABB", description = "Axis-Aligned Bounding Box (AABB) wrapper for culling and spatial queries.")
public class JSBoxAABB {
    @JSHideFromDoc
    private final CullingAABB cullingAABB;

    @JSHideFromDoc
    public JSBoxAABB(CullingAABB cullingAABB) {
        this.cullingAABB = cullingAABB;
    }

    @JSCodingConstructor(description = "Create AABB from min and max points", paramNames = {"aabbMin", "aabbMax"})
    public JSBoxAABB(JSVector3f aabbMin, JSVector3f aabbMax) {
        this.cullingAABB = new CullingAABB(aabbMin.getJavaVector3f(), aabbMax.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Create a transformation matrix representing this AABB")
    public JSMatrix4f createAABBTransformMatrix() {
        return new JSMatrix4f(this.cullingAABB.createAABBTransformMatrix());
    }

    @JSCodingFunctionOrMethod(description = "Get minimum corner of the AABB")
    public JSVector3f getAabbMin() {
        return new JSVector3f(this.cullingAABB.getAabbMin());
    }

    @JSCodingFunctionOrMethod(description = "Get maximum corner of the AABB")
    public JSVector3f getAabbMax() {
        return new JSVector3f(this.cullingAABB.getAabbMax());
    }
    @JSCodingFunctionOrMethod(description = "Get underlying Java CullingAABB object (unsafe)")
    @JSHideFromDoc
    public CullingAABB getJavaCullingAABB() {
        return this.cullingAABB;
    }
}