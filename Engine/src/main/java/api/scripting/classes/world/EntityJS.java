package api.scripting.classes.world;

import api.scripting.classes.util.Vec3f;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;

@JSTypeDoc(description = "Entity in physical world", order = 4)
public final class EntityJS {
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

    public void remove() {
        this.getWorldItem().setDead();
    }

    WorldItem getWorldItem() {
        return this.worldItem;
    }
}
