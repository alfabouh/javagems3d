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

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MeshNode3D<T extends IMesh> extends MeshNode<T> {
    private Material material;

    public MeshNode3D(@NotNull T meshData) {
        this(meshData, new Material());
    }

    public MeshNode3D(@NotNull T meshData, @NotNull Material material) {
        super(meshData);
        this.material = material;
    }

    @Override
    public void clear() {
        super.clear();
        this.material = null;
    }

    public boolean hasTransparency() {
        return this.getMaterial().hasTransparency();
    }

    public void setMaterial(@NotNull Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }
}
