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

package javagems3d.system.resources.assets.models.mesh.structures.solid;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MeshGroup extends MeshStructure3D<RenderMesh> {
    public static final String POSTFIX = "_group";
    private boolean hasSubMeshAABs;

    public MeshGroup(@Nullable List<MeshNode3D<RenderMesh>> meshNodes) {
        this.hasSubMeshAABs = false;
        if (meshNodes != null) {
            this.putNodes(meshNodes);
        }
    }

    @SafeVarargs
    public MeshGroup(MeshNode3D<RenderMesh>... t) {
        this(Arrays.asList(t));
    }

    public MeshGroup(RenderMesh renderMesh) {
        this(new MeshNode3D<>(renderMesh, new Material()));
    }

    public MeshGroup() {
        this((List<MeshNode3D<RenderMesh>>) null);
    }

    public boolean isHasSubMeshAABs() {
        return this.hasSubMeshAABs;
    }

    public MeshGroup setHasSubMeshAABs(boolean hasSubMeshAABs) {
        this.hasSubMeshAABs = hasSubMeshAABs;
        return this;
    }

    @Override
    public boolean canBeUsedInIndirectRendering() {
        return false;
    }

    @Override
    public void clear() {
        super.clear();
    }
}