package ru.jgems3d.engine.graphics.opengl.rendering.items;

import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.environment.light.Light;

import java.util.List;

public interface ILightsKeeper {
    void addLight(Light light);

    void removeLight(Light light);

    List<Light> getLightsList();

    default void adjustLightsTranslation(Vector3f pos, Vector3f offset) {
        for (Light l : this.getLightsList()) {
            l.setLightPos(pos);
            l.setOffset(offset);
        }
    }

    @SuppressWarnings("all")
    default boolean removeLightById(int id) {
        if (this.getLightsList().size() <= id) {
            this.removeLight(this.getLightsList().get(id));
            return true;
        }
        return false;
    }

    default boolean hasLights() {
        return !this.getLightsList().isEmpty();
    }
}
