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
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JSCodingClass(binding = "JSMeshNode3D", description = "Wrapper for MeshNode3D providing access to 3D mesh data and material management.")
public class JSMeshNode3D implements JSMeshNodeI {
    @JSHideFromDoc
    private final MeshNode3D<?> meshNode3D;

    @JSHideFromDoc
    public JSMeshNode3D(MeshNode3D<?> meshNode3D) {
        this.meshNode3D = meshNode3D;
    }

    @JSCodingFunctionOrMethod(description = "Check if the mesh has transparency")
    public boolean hasTransparency() {
        return this.meshNode3D.hasTransparency();
    }

    @JSCodingFunctionOrMethod(description = "Set material for this mesh node", paramNames = {"material"})
    public void setMaterial(@NotNull JSMaterial material) {
        this.meshNode3D.setMaterial(material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Get current material of this mesh node")
    public JSMaterial getMaterial() {
        return new JSMaterial(this.meshNode3D.getMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Clear mesh node data")
    public void clear() {
        this.meshNode3D.clear();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java MeshNode3D (unsafe)")
    @JSHideFromDoc
    public MeshNode3D<?> getJavaMeshNode3D() {
        return this.meshNode3D;
    }

    @JSHideFromDoc
    @Override
    public MeshNode<?> meshNode() {
        return this.meshNode3D;
    }
}