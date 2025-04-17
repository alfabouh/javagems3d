package api.scripting.classes.world;

import api.scripting.classes.util.Vec3f;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.NotNull;

public final class PropJS {
    private final SceneProp sceneProp;

    public PropJS(@NotNull SceneProp sceneProp) {
        this.sceneProp = sceneProp;
    }

    public void setPosition(Vec3f vec3f) {
        if (!this.getSceneObject().hasModel()) {
            return;
        }
        Pose3D pose3D = this.getSceneObject().getModel().getPose();
        pose3D.setPosition(vec3f.createJOML());
    }

    public void setRotation(Vec3f vec3f) {
        if (!this.getSceneObject().hasModel()) {
            return;
        }
        Pose3D pose3D = this.getSceneObject().getModel().getPose();
        pose3D.setRotation(vec3f.createJOML());
    }

    public void setScaling(Vec3f vec3f) {
        if (!this.getSceneObject().hasModel()) {
            return;
        }
        Pose3D pose3D = this.getSceneObject().getModel().getPose();
        pose3D.setScaling(vec3f.createJOML());
    }

    public void remove() {
        JGemsWorldHelper.removePropFromScene(this.getSceneObject());
    }

    SceneProp getSceneObject() {
        return this.sceneProp;
    }
}