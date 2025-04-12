package javagems3d.graphics.objects;

import javagems3d.graphics.environment.lights.ILightAttached;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Set;

public interface ILighted {
    default void addLightAttachment(ILightAttached light) {
        if (light == null) {
            return;
        }
        light.attachTo(this);
        this.getAttachedLights().add(light);
    }

    default void removeLightAttachment(ILightAttached light) {
        if (light == null) {
            return;
        }
        light.detach();
        this.getAttachedLights().remove(light);
    }

    @NotNull Set<ILightAttached> getAttachedLights();

    Vector3f getPositionToAttachLights();

    default boolean isLightAttached(ILightAttached light) {
        return this.getAttachedLights().contains(light);
    }

    default boolean hasLights() {
        return !this.getAttachedLights().isEmpty();
    }
}
