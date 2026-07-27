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

package api.scripting.coding.env.internal.util.resources.instances.models.poly;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes.JSMeshNode2D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes.JSMeshNode3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure2D;
import javagems3d.system.resources.assets.models.mesh.structures.flat.MeshGui;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;

import java.util.List;

@JSCodingClass(binding = "JSMesh2D", description = "2D mesh wrapper over MeshGui")
public class JSMesh2D implements JSCanBeCachedInMemory, JSMeshStructure2D {
    @JSHideFromDoc
    private final MeshGui meshGui;

    @JSCodingConstructor(description = "Create empty mesh")
    public JSMesh2D() {
        this.meshGui = new MeshGui();
    }

    @JSCodingConstructor(description = "Create mesh from render mesh", paramNames = {"renderMesh"})
    public JSMesh2D(RenderMesh renderMesh) {
        this.meshGui = new MeshGui(renderMesh);
    }

    @JSCodingConstructor(description = "Wrap existing MeshGui", paramNames = {"mesh"})
    public JSMesh2D(MeshGui mesh) {
        this.meshGui = mesh;
    }

    @JSCodingFunctionOrMethod(description = "Clear mesh")
    public JSMesh2D clear() {
        this.meshGui.clear();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying MeshGui")
    public MeshGui getJavaMeshGui() {
        return this.meshGui;
    }

    @JSCodingFunctionOrMethod(description = "Nodes list")
    public List<JSMeshNode2D> getNodes() {
        return this.meshGui.getNodes().stream().map(JSMeshNode2D::new).toList();
    }

    @JSHideFromDoc
    @Override
    public MeshStructure2D getJavaMeshStructure2D() {
        return this.meshGui;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return "JSMesh2D[" + meshGui + "]";
    }
}