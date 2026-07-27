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

package javagems3d.system.resources.assets.models.mesh.structures.flat;

import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure2D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeshGui extends MeshStructure2D {
    public MeshGui(@Nullable List<MeshNode2D> meshNodes) {
        if (meshNodes != null) {
            this.putNodes(meshNodes);
        }
    }

    public MeshGui(RenderMesh renderMesh) {
        this(new MeshNode2D(renderMesh));
    }

    public MeshGui(MeshNode2D... t) {
        this(Arrays.asList(t));
    }

    public MeshGui() {
        this((List<MeshNode2D>) null);
    }
}
