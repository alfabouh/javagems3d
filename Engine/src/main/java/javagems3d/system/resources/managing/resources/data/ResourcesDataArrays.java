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

import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class ResourcesDataArrays {
    private final MeshBuffersDataArray meshBuffersDataArray;
    private final BindlessTexturesDataArray bindlessTexturesDataArray;
    private final Set<MeshStructure3D<?>> meshesWithAnimation;

    public ResourcesDataArrays(@NotNull MeshBuffersDataArray meshBuffersDataArray, @NotNull BindlessTexturesDataArray bindlessTexturesDataArray) {
        this.meshBuffersDataArray = meshBuffersDataArray;
        this.bindlessTexturesDataArray = bindlessTexturesDataArray;
        this.meshesWithAnimation = new HashSet<>();
    }

    public Set<MeshStructure3D<?>> getMeshesWithAnimation() {
        return this.meshesWithAnimation;
    }

    public void clearAll() {
        this.getBindlessTexturesArray().clear();
        this.getMeshBuffersDataArray().clear();
        this.getMeshesWithAnimation().clear();
    }

    public void clearMeshContainers() {
        this.getMeshBuffersDataArray().clear();
        this.getMeshesWithAnimation().clear();
    }

    public MeshBuffersDataArray getMeshBuffersDataArray() {
        return this.meshBuffersDataArray;
    }

    public BindlessTexturesDataArray getBindlessTexturesArray() {
        return this.bindlessTexturesDataArray;
    }
}
