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

package javagems3d.system.resources.managing.resources.data.bindless_rendering_cache;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;

import java.util.*;

public final class MeshBuffersDataCache implements IBindlessDataCache {
    private final Map<Material, Integer> materialsIdMap;
    private final List<Material> materials;
    private final Set<MeshBuffer> meshBuffers;

    public MeshBuffersDataCache() {
        this.materialsIdMap = new HashMap<>();
        this.materials = new ArrayList<>();
        this.meshBuffers = new HashSet<>();
    }

    public void writeData(Set<MeshBuffersDataArray> arraySet) {
        this.addMaterial(new Material());
        int i = this.getMaterials().size();
        for (MeshBuffersDataArray meshBuffersDataArray : arraySet) {
            for (MeshBuffer meshBuffer : meshBuffersDataArray.getMeshBuffers()) {
                this.addMeshBuffer(meshBuffer);
            }
            for (Material material : meshBuffersDataArray.getMaterials()) {
                this.materialsIdMap.put(material, i++);
                this.addMaterial(material);
            }
        }
    }

    public void clear() {
        this.meshBuffers.clear();
        this.materials.clear();
        this.materialsIdMap.clear();
    }

    public int getMaterialId(Material material) {
        return this.materialsIdMap.getOrDefault(material, 0);
    }

    public void addMeshBuffer(MeshBuffer meshBuffer) {
        this.meshBuffers.add(meshBuffer);
    }

    public void addMaterial(Material material) {
        this.materials.add(material);
    }

    public Set<MeshBuffer> getMeshBuffers() {
        return this.meshBuffers;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }
}
