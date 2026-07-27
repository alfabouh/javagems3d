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

package javagems3d.system.resources.managing.resources.data.arrays;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;

import java.util.*;

public final class MeshBuffersDataArray implements IDataArray {
    private final List<Material> materials;
    private final Set<MeshBuffer> meshBuffers;

    private MeshBuffersDataArray(List<Material> materials, Set<MeshBuffer> meshBuffers) {
        this.materials = materials;
        this.meshBuffers = meshBuffers;
    }

    public MeshBuffersDataArray() {
        this.materials = new ArrayList<>();
        this.meshBuffers = new HashSet<>();
    }

    public void clear() {
        this.getMeshBuffers().clear();
        this.getMaterials().clear();
    }

    public int getTotalMaterials() {
        return this.getMaterials().size();
    }

    public void addMeshBuffer(MeshBuffer meshBuffer) {
        this.getMeshBuffers().add(meshBuffer);
    }

    public void addMaterial(Material material) {
        this.getMaterials().add(material);
    }

    public Set<MeshBuffer> getMeshBuffers() {
        return this.meshBuffers;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public MeshBuffersDataArray copy() {
        return new MeshBuffersDataArray(new ArrayList<>(this.materials), new HashSet<>(this.meshBuffers));
    }
}