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

package api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.JSMaterial;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSMeshNode2D", description = "Wrapper for MeshNode2D providing 2D mesh operations.")
public class JSMeshNode2D implements JSMeshNodeI {
    @JSHideFromDoc
    private final MeshNode2D meshNode2D;

    @JSHideFromDoc
    public JSMeshNode2D(MeshNode2D meshNode2D) {
        this.meshNode2D = meshNode2D;
    }

    @JSCodingFunctionOrMethod(description = "Clear mesh node data")
    public void clear() {
        this.meshNode2D.clear();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java MeshNode2D (unsafe)")
    @JSHideFromDoc
    public MeshNode2D getJavaMeshNode2D() {
        return this.meshNode2D;
    }

    @JSHideFromDoc
    @Override
    public MeshNode<?> meshNode() {
        return this.meshNode2D;
    }
}