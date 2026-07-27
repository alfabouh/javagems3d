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

package javagems3d.system.resources.assets.models.mesh.structures.nodes;

import javagems3d.system.resources.assets.models.mesh.IMesh;
import org.jetbrains.annotations.NotNull;

public abstract class MeshNode<T extends IMesh> {
    private T meshData;

    public MeshNode(@NotNull T meshData) {
        this.meshData = meshData;
    }

    public void clear() {
        if (this.getMeshData() != null) {
            this.getMeshData().clearMesh();
        }
        this.meshData = null;
    }

    public void clearData(boolean keepTrianglesInMemory) {
        if (this.getMeshData() != null) {
            this.getMeshData().clearData(keepTrianglesInMemory);
        }
    }

    public T getMeshData() {
        return this.meshData;
    }

    public void setMeshStructure(T meshData) {
        this.meshData = meshData;
    }
}