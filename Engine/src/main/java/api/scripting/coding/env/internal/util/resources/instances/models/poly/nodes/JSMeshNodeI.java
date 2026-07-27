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
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode;

import java.util.List;

@JSCodingClass(binding = "JSMeshNodeI", description = "Interface for accessing mesh node data and vertex information.")
public interface JSMeshNodeI {

    @JSCodingFunctionOrMethod(description = "Get list of vertex indexes for the mesh")
    default List<Integer> getVertexIndexes() {
        return this.meshNode().getMeshData().getVertexIndexes();
    }

    @JSCodingFunctionOrMethod(description = "Get list of vertex positions (x, y, z triplets)")
    default List<Float> getVertexPositions() {
        return this.meshNode().getMeshData().getVertexPositions();
    }

    @JSCodingFunctionOrMethod(description = "Get total number of vertices in the mesh")
    default int totalVertexes() {
        return this.meshNode().getMeshData().totalVertexes();
    }

    @JSCodingFunctionOrMethod(description = "Get number of vertex indexes in the mesh")
    default int numVertexIndexes() {
        return this.meshNode().getMeshData().numVertexIndexes();
    }

    @JSCodingFunctionOrMethod(description = "Get number of position values in the mesh (3 per vertex)")
    default int numPositions() {
        return this.meshNode().getMeshData().numPositions();
    }

    @JSHideFromDoc
    MeshNode<?> meshNode();
}