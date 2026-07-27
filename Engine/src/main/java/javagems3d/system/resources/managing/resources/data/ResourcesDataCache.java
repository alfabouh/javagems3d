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

package javagems3d.system.resources.managing.resources.data;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;
import javagems3d.system.resources.managing.resources.data.bindless_rendering_cache.BindlessTexturesDataCache;
import javagems3d.system.resources.managing.resources.data.bindless_rendering_cache.MeshBuffersDataCache;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ResourcesDataCache {
    private final MeshBuffersDataCache meshBuffersDataArray;
    private final BindlessTexturesDataCache bindlessTexturesDataArray;

    public ResourcesDataCache(@NotNull MeshBuffersDataCache meshBuffersDataCache, @NotNull BindlessTexturesDataCache bindlessTexturesDataCache) {
        this.meshBuffersDataArray = meshBuffersDataCache;
        this.bindlessTexturesDataArray = bindlessTexturesDataCache;
    }

    public void writeAll(Collection<ResourcesDataArrays> arrays) {
        Set<BindlessTexturesDataArray> bindlessTexturesDataArraySet = new HashSet<>();
        Set<MeshBuffersDataArray> meshBuffersDataArraySet = new HashSet<>();
        arrays.forEach(e -> {
            bindlessTexturesDataArraySet.add(e.getBindlessTexturesArray().copy());
            meshBuffersDataArraySet.add(e.getMeshBuffersDataArray().copy());
            //e.getBindlessTexturesArray().clear();
            //e.getMeshBuffersDataArray().clear();
        });
        {
            final MeshBuffersDataArray defCube = new MeshBuffersDataArray();
            defCube.addMeshBuffer(ResourceManager.DEFAULT_CUBE_MESHBUFFER());
            defCube.addMaterial(new Material());
            meshBuffersDataArraySet.add(defCube);
        }
        {
            final MeshBuffersDataArray defParticle = new MeshBuffersDataArray();
            defParticle.addMeshBuffer(ResourceManager.GLOBAL_PARTICLE_MESHBUFFER());
            defParticle.addMaterial(new Material());
            meshBuffersDataArraySet.add(defParticle);
        }
        this.clearAll();
        this.getBindlessTexturesCache().writeData(bindlessTexturesDataArraySet);
        this.getMeshBuffersDataCache().writeData(meshBuffersDataArraySet);

      // System.out.println(this.getMeshBuffersDataCache().getMeshBuffers().size());
      // System.out.println("F");
    }

    public void writeAll(ResourcesDataArrays... arrays) {
        List<ResourcesDataArrays> arrays1 = new ArrayList<>(Arrays.asList(arrays));
        this.writeAll(arrays1);
    }

    public void clearAll() {
        this.getBindlessTexturesCache().clear();
        this.getMeshBuffersDataCache().clear();
    }

    public MeshBuffersDataCache getMeshBuffersDataCache() {
        return this.meshBuffersDataArray;
    }

    public BindlessTexturesDataCache getBindlessTexturesCache() {
        return this.bindlessTexturesDataArray;
    }
}
