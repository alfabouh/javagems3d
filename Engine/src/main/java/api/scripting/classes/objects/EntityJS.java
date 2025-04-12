package api.scripting.classes.objects;

import api.scripting.classes.util.Vec3f;
import javagems3d.physics.entities.bullet.JGemsBody;
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

    public WorldItem getWorldItem() {
        return this.worldItem;
    }
}
