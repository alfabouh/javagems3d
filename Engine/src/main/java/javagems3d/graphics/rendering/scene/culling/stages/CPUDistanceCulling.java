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

package javagems3d.graphics.rendering.scene.culling.stages;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Set;

public class CPUDistanceCulling implements ICullingAlgorithm {
    private float distanceToCull;
    private ICamera camera;

    public CPUDistanceCulling() {
        this(-1.0f);
    }

    public CPUDistanceCulling(float distanceToCull) {
        this.distanceToCull = distanceToCull;
        this.camera = null;
    }

    public float getDistanceToCull() {
        return distanceToCull;
    }

    public void setDistanceToCull(float distanceToCull) {
        this.distanceToCull = distanceToCull;
    }

    public ICamera getCamera() {
        return camera;
    }

    public void setCamera(@Nullable ICamera camera) {
        this.camera = camera;
    }

    public boolean test(@NotNull ICulled culled) {
        if (culled instanceof SceneObject sceneObject) {
            if (!sceneObject.canBeRendered() || !sceneObject.hasModel()) {
                return false;
            }
            if (culled.getCullingRules().isIgnoreDistanceCulling()) {
                return true;
            }
            Vector3f position = sceneObject.getModel().getPose().getPosition();
            if (this.getDistanceToCull() > 0.0f && position.distance(this.getCamera().getCamPosition()) >= this.getDistanceToCull()) {
                return false;
            }
            RenderAttributes renderAttributes = sceneObject.getRenderAttributes();
            float distance = renderAttributes.getProperties().getFloat(JGemsRenderProperties.KEY_RENDER_DISTANCE, 256.0f);
            return distance < 0.0f || position.distance(camera.getCamPosition()) < distance;
        }
        return true;
    }

    @Override
    public void filter(@NotNull Collection<? extends ICulled> sceneObjects) {
        if (this.getCamera() == null) {
            Log.get().warn("Tried to do distance culling with NULL camera");
            return;
        }
        sceneObjects.removeIf(e -> !this.test(e));
    }
}
