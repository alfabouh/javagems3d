package api.scripting.classes.world;

import api.scripting.classes.util.Vec3f;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSTypeDoc(description = "Entity in physical world", priority = JSTypeDoc.Priority.MED)
public final class EntityJS extends ObjectJS {
    private final WorldItem worldItem;

    public EntityJS(@NotNull WorldItem worldItem) {
        this.worldItem = worldItem;
    }

    @JSMethodDoc(description = "Set entity location", args = {"Position"}, order = 0)
    public void setPosition(Vec3f vec3f) {
        this.getWorldItem().setPosition(vec3f.createJOML());
    }

    @JSMethodDoc(description = "Set entity rotation. Euler XYZ", args = {"Rotation"}, order = 1)
    public void setRotation(Vec3f vec3f) {
        this.getWorldItem().setRotation(vec3f.createJOML());
    }

    @JSMethodDoc(description = "Set entity scaling", args = {"Scaling"}, order = 2)
    public void setScaling(Vec3f vec3f) {
        this.getWorldItem().setScaling(vec3f.createJOML());
    }

    @JSMethodDoc(description = "Get entity location", args = {}, order = 3)
    public Vec3f getPosition() {
        Vector3f vector3f = this.getWorldItem().getPosition();
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    @JSMethodDoc(description = "Get entity rotation. Euler XYZ", args = {}, order = 4)
    public Vec3f getRotation() {
        Vector3f vector3f = this.getWorldItem().getRotation();
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    @JSMethodDoc(description = "Get entity scaling", args = {}, order = 5)
    public Vec3f getScaling() {
        Vector3f vector3f = this.getWorldItem().getScaling();
        return new Vec3f(vector3f.x, vector3f.y, vector3f.z);
    }

    public void remove() {
        this.getWorldItem().setDead();
    }

    public WorldItem getWorldItem() {
        return this.worldItem;
    }
}
