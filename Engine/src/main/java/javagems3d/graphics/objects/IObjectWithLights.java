package javagems3d.graphics.objects;

import javagems3d.graphics.environment.lights.ILightAttachable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Set;

public interface IObjectWithLights {
    default void addLightAttachment(ILightAttachable light) {
        if (light == null) {
            return;
        }
        light.attachTo(this);
        this.getAttachedLights().add(light);
    }

    default void removeLightAttachment(ILightAttachable light) {
        if (light == null) {
            return;
        }
        light.detach();
        this.getAttachedLights().remove(light);
    }

    @NotNull Set<ILightAttachable> getAttachedLights();

    Vector3f getPositionToAttachLights();
    Vector3f getRotationAngle();

    default boolean isLightAttached(ILightAttachable light) {
        return this.getAttachedLights().contains(light);
    }

    default boolean hasLights() {
        return !this.getAttachedLights().isEmpty();
    }
}
