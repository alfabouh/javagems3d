package api.scripting.classes.world.objects;

import api.scripting.classes.util.Vec3f;
import api.scripting.classes.world.ObjectJS;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSTypeDoc(description = "Prop in scene world", priority = JSTypeDoc.Priority.MED)
public class PropJS extends ObjectJS {
    private final SceneProp sceneProp;

    public PropJS(@NotNull SceneProp sceneProp) {
        this.sceneProp = sceneProp;
    }

    @JSMethodDoc(description = "Get prop location", args = {}, order = 3)
    public Vec3f getPosition() {
        Vector3f vector3f = this.getSceneObject().getModel().getPose().getPosition();
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    @JSMethodDoc(description = "Set prop location", args = {"position"}, order = 0)
    public void setPosition(Vec3f vec3f) {
        if (!this.getSceneObject().hasModel()) {
            return;
        }
        Pose3D pose3D = this.getSceneObject().getModel().getPose();
        pose3D.setPosition(vec3f.createJOML());
    }

    @JSMethodDoc(description = "Get prop rotation. Euler XYZ", args = {}, order = 4)
    public Vec3f getRotation() {
        Vector3f vector3f = this.getSceneObject().getModel().getPose().getRotation();
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    @JSMethodDoc(description = "Set prop rotation. Euler XYZ", args = {"rotation"}, order = 1)
    public void setRotation(Vec3f vec3f) {
        if (!this.getSceneObject().hasModel()) {
            return;
        }
        Pose3D pose3D = this.getSceneObject().getModel().getPose();
        pose3D.setRotation(vec3f.createJOML());
    }

    @JSMethodDoc(description = "Get prop scaling", args = {}, order = 5)
    public Vec3f getScaling() {
        Vector3f vector3f = this.getSceneObject().getModel().getPose().getScaling();
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    @JSMethodDoc(description = "Set prop scaling", args = {"scaling"}, order = 2)
    public void setScaling(Vec3f vec3f) {
        if (!this.getSceneObject().hasModel()) {
            return;
        }
        Pose3D pose3D = this.getSceneObject().getModel().getPose();
        pose3D.setScaling(vec3f.createJOML());
    }

    @JSMethodDoc(description = "Mark object as dead. Dead objects will be removed from the world.", args = {}, order = 6)
    public void setDead() {
        this.getSceneObject().setDead();
    }

    @JSMethodDoc(description = "Check if object is marked as dead.", args = {}, order = 7)
    public boolean isDead() {
        return this.getSceneObject().isDead();
    }

    public void remove() {
        JGemsHelper.world().removeProp(this.getSceneObject());
    }

    SceneProp getSceneObject() {
        return this.sceneProp;
    }
}