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

package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class MeshStructure2D extends MeshStructure<RenderMesh, MeshNode2D> {
    public MeshStructure2D() {
    }

    public int @NotNull [] getLayersToInit() {
        return new int[] {0};
    }

    public void putNodes(List<MeshNode2D> list) {
        for (MeshNode2D m : list) {
            this.putNode(0, m);
        }
    }

    public void putSolidNode(MeshNode2D meshNode3D) {
        this.putNode(0, meshNode3D);
    }

    public List<MeshNode2D> getNodes() {
        return this.getNodes(0);
    }
}
