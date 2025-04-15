package api.scripting.classes.world;

import api.scripting.classes.util.Vec3f;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;

public final class EntityJS {
    private final WorldItem worldItem;

    public EntityJS(@NotNull WorldItem worldItem) {
        this.worldItem = worldItem;
    }

    public void setPosition(Vec3f vec3f) {
        this.getWorldItem().setPosition(vec3f.createJOML());
    }

    public void setRotation(Vec3f vec3f) {
        this.getWorldItem().setRotation(vec3f.createJOML());
    }

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
