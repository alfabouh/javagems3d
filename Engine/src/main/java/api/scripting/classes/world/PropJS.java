package api.scripting.classes.world;

import api.scripting.classes.util.Vec3f;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSTypeDoc(description = "Prop in scene world", priority = JSTypeDoc.Priority.MED)
public final class PropJS extends ObjectJS {
    private final SceneProp sceneProp;

    public PropJS(@NotNull SceneProp sceneProp) {
        this.sceneProp = sceneProp;
    }

    @JSMethodDoc(description = "Set prop location", args = {"Position"}, order = 0)
    public void setPosition(Vec3f vec3f) {
        if (!this.getSceneObject().hasModel()) {
            return;
        }
        Pose3D pose3D = this.getSceneObject().getModel().getPose();
        pose3D.setPosition(vec3f.createJOML());
    }

    @JSMethodDoc(description = "Set prop rotation. Euler XYZ", args = {"Rotation"}, order = 1)
    public void setRotation(Vec3f vec3f) {
        if (!this.getSceneObject().hasModel()) {
            return;
        }
        Pose3D pose3D = this.getSceneObject().getModel().getPose();
        pose3D.setRotation(vec3f.createJOML());
    }

    @JSMethodDoc(description = "Set prop scaling", args = {"Scaling"}, order = 2)
    public void setScaling(Vec3f vec3f) {
        if (!this.getSceneObject().hasModel()) {
            return;
        }
        Pose3D pose3D = this.getSceneObject().getModel().getPose();
        pose3D.setScaling(vec3f.createJOML());
    }

    @JSMethodDoc(description = "Get prop location", args = {}, order = 3)
    public Vec3f getPosition() {
        Vector3f vector3f = this.getSceneObject().getModel().getPose().getPosition();
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    @JSMethodDoc(description = "Get prop rotation. Euler XYZ", args = {}, order = 4)
    public Vec3f getRotation() {
        Vector3f vector3f = this.getSceneObject().getModel().getPose().getRotation();
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    @JSMethodDoc(description = "Get prop scaling", args = {}, order = 5)
    public Vec3f getScaling() {
        Vector3f vector3f = this.getSceneObject().getModel().getPose().getScaling();
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    public void remove() {
        JGemsHelper.world().removeProp(this.getSceneObject());
    }

    public SceneProp getSceneObject() {
        return this.sceneProp;
    }
}