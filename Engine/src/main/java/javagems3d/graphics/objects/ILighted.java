package javagems3d.graphics.objects;

import javagems3d.graphics.environment.lights.ILightAttached;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Set;

public interface ILighted {
    default void addLight(ILightAttached light) {
        light.attachTo(this);
        this.getAttachedLights().add(light);
    }

    default void removeLight(ILightAttached light) {
        light.detach();
        this.getAttachedLights().remove(light);
    }

    default boolean isLightAttached(ILightAttached light) {
        return this.getAttachedLights().contains(light);
    }

    @NotNull Set<ILightAttached> getAttachedLights();

    Vector3f getPositionToAttachLights();

    default boolean hasLights() {
        return !this.getAttachedLights().isEmpty();
    }
}
