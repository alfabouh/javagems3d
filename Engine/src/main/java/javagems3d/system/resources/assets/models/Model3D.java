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

package javagems3d.system.resources.assets.models;

import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class Model3D extends Model<Pose3D, MeshStructure3D<? extends IMesh>> {
    public Model3D(@NotNull Pose3D pose, @Nullable MeshStructure3D<? extends IMesh> meshStructure) {
        super(pose, meshStructure);
    }

    public Model3D(@NotNull Model<Pose3D, MeshStructure3D<? extends IMesh>> model) {
        super(model);
    }

    public Model3D(@NotNull Model<Pose3D, MeshStructure3D<? extends IMesh>> model, @NotNull Pose3D pose) {
        super(model, pose);
    }

    @SuppressWarnings("all")
    public <R extends MeshStructure3D<? extends IMesh>> R getMeshStructureCast() {
        try {
            return (R) this.getMeshStructure();
        } catch (ClassCastException e) {
            throw new JGemsRuntimeException("Unable to cast!\n" + e.getMessage());
        }
    }
}
