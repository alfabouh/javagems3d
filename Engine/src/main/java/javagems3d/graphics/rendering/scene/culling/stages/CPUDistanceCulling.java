package javagems3d.graphics.rendering.scene.culling.stages;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import logger.Log;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

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

    public boolean test(ICulled culled) {
        if (culled.getCullingRules().isIgnoreDistanceCulling()) {
            return true;
        }
        SceneObject sceneObject = (SceneObject) culled;
        Vector3f position = sceneObject.getModel().getPose().getPosition();
        if (this.getDistanceToCull() > 0.0f && position.distance(this.getCamera().getCamPosition()) >= this.getDistanceToCull()) {
            return false;
        }
        RenderAttributes renderAttributes = sceneObject.getRenderAttributes();
        float distance = renderAttributes.getProperties().getFloat(JGemsRenderProperties.KEY_RENDER_DISTANCE);
        return !(distance > 0.0f) || !(position.distance(camera.getCamPosition()) >= distance);
    }

    @Override
    public void filter(Set<SceneObject> sceneObjects) {
        if (this.getCamera() == null) {
            Log.get().warn("Tried to do distance culling with NULL camera");
            return;
        }
        sceneObjects.removeIf(e -> !this.test(e));
    }
}
