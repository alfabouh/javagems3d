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

package javagems3d.graphics.objects;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.Nullable;

public interface IModeled extends IAnimated {
    Model3D getModel();
    void updateAnimation();

    default @Nullable CullingAABB pickAABBDataFromMesh() {
        if (!this.hasModel()) {
            return null;
        }
        Pose3D pose3D = this.getModel().getPose();
        if (this.isAnimated()) {
            return this.getModel().getMeshStructure().getMeshAABBDataForAnimation(this.getAnimationData().getCurrentAnimation()).getNormalizedAABB(pose3D);
        }
        MeshBoundingBoxData meshBoundingBoxData = this.getModel().getMeshStructure().getMeshAABBData();
        if (meshBoundingBoxData == null) {
            return null;
        }
        return meshBoundingBoxData.getNormalizedAABB(pose3D);
    }

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }
}
